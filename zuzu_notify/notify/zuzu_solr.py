# encoding: utf-8


import sys
import logging
import pysolarized
import aiosolr

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1

if IS_LOCAL:
    from notify import timeutils, json_utils, constants
else:
    import timeutils, json_utils, constants

class SolrClient(object):
    def __init__(self, solr_url):
        self.solr_url = solr_url
        self.solr = pysolarized.Solr(endpoints=self.solr_url, http_cache=False)
        self.logger = logging.getLogger(__name__)
        
    def _error_callback(self, document=None, item_id=None, query=None):
        """
        Sends error
        """

    def _get_rows(self, query, filters):
        results = self.solr.query(query=query, filters=filters, rows=0)
        return results.results_count

    def get_new_post_items(self, post_time):
        query = "*:*"
        filters = {}
        filters["post_time"] = "["+post_time+" TO *]"
        filters["-parent"] = "*"
        columns = ["*"]
        sort = ["post_time asc"]
        rows = self._get_rows(query, filters)
        if rows > 0:
            r = self.solr.query(query=query, filters=filters, columns= columns, rows=rows, sort=sort)
            docs = r.documents
            return docs
        else:
            return None

    def delete_old_items(self, post_time):
        query = "post_time:[ * TO " + post_time +" ]"
        columns = ["id"]
        rows = self._get_rows(query, filters=None)
        if rows > 0:
            r = self.solr.query(query=query, columns= columns, rows=rows)
            docs = r.documents
            if docs is not None:
                for item in docs:
                    self.solr.delete(item["id"])
                    self.solr.commit()


    def add(self, item):
        self.solr.add(item)

    def commit(self):
        self.solr.commit()


class AsyncSolrClient(object):
    def __init__(self, solr_url):
        self.solr_url = solr_url
        self.solr = aiosolr.Solr(self.solr_url)
        self.logger = logging.getLogger(__name__)

        self.nearby_fileds = ["nearby_train", "nearby_mrt", "nearby_bus", "nearby_thsr"]

        self.current_notify_time = timeutils.get_now()
        self.current_notify_time = self.current_notify_time.replace(second=0, minute=0)
        self.logger.info("current zuzu notify time: " + timeutils.get_time_str(self.current_notify_time, timeutils.UTC_FORMT))

        if self.current_notify_time.hour == 0:
            self.NOTIFY_INTERVAL_HOURS = 8
            self.NOTIFY_ITEMS_LIMIT = 20
        else:
            self.NOTIFY_INTERVAL_HOURS = 1
            self.NOTIFY_ITEMS_LIMIT = 10

        self.logger.info("NOTIFY_INTERVAL_HOURS: " + str(self.NOTIFY_INTERVAL_HOURS))
        self.logger.info("NOTIFY_ITEMS_LIMIT: " + str(self.NOTIFY_ITEMS_LIMIT))
        self.current_query_post_time = self.get_current_query_post_time()

        if constants.PRODUCT_MODE == False and constants.TEST_PERFORMANCE == True:
            self.price_seq = 0

    def get_current_query_post_time(self):
        is_complement_mode = False
        assign_query_post_time = None
        for arg in sys.argv:
            if arg.lower() == constants.complement_mode:
                is_complement_mode = True
            else:
                try:
                    dt = timeutils.convert_time(arg, timeutils.UTC_FORMT)
                    if dt:
                        assign_query_post_time = arg
                except ValueError:
                    pass

        current_query_post_time = timeutils.get_hours_ago(dt=self.current_notify_time, hours= self.NOTIFY_INTERVAL_HOURS)
        if is_complement_mode == True:
            self.logger.warn("is_complement_mode = true")
            if assign_query_post_time:
                self.logger.warn("using assigned query post time: " + assign_query_post_time)
                current_query_post_time = timeutils.convert_time(assign_query_post_time, timeutils.UTC_FORMT)
            else:
                json = json_utils.get_notify_json()
                fail_notify_time = json.get("fail_query_post_time")
                self.logger.warn("using fail query post time: " + fail_notify_time)
                current_query_post_time = timeutils.convert_time(fail_notify_time, timeutils.UTC_FORMT)

        self.logger.info("current query post time: " + timeutils.get_time_str(current_query_post_time, timeutils.UTC_FORMT))
        return current_query_post_time


    async def query(self, query, filters=None, columns=None, sort=None, start=0, rows=30):
        if not columns:
            columns = ["*", "score"]

        fields = {#"q": query,
                 "json.nl" :"map",           # Return facets as JSON objects
                 "fl": ",".join(columns),    # Return score along with results
                 "start": str(start),
                 "rows": str(rows),
                 "wt": "json"}

        # Prepare filters
        if not filters is None:
            filter_list = []
            for filter_field, value in filters.items():
                filter_list.append("%s:%s" % (filter_field, value))
            fields["fq"] = " AND ".join(filter_list)

        # Append sorting parameters
        if not sort is None:
            fields["sort"] = ",".join(sort)


        results = await self.solr.search(query, **fields)
        return results

    async def get_notify_items(self, notifier):
        self.logger.info("get_notify_items for user: "+ notifier.user_id)

        query = self.get_query(notifier)

        query_post_time = self.next_query_post_time(notifier)

        q = query["query"]
        filters = query["filters"]
        filters["post_time"] = "["+query_post_time+" TO *]"
        columns = ["id, title, price, size house_type, purpose_type, addr, img, post_time"]
        sort = ["price asc, post_time desc"]
        results = await self.query(query=q, filters=filters,columns=columns,sort=sort,start=0,rows=self.NOTIFY_ITEMS_LIMIT)

        notify_items = results.docs

        if notify_items is None or len(notify_items) < 1:
            self.logger.info("no new notify items for "+notifier.user_id)
            notifier.last_notify_time = self.current_notify_time
            return notify_items
        else:
            self.logger.info("found "+ str(len(notify_items))+" new notify items for "+notifier.user_id)

        self.logger.info("query criteria for "+notifier.user_id+": query=" + q + ", filters="+ str(filters))


        latest_notify_time = None
        for item in notify_items:
            item["item_id"] = item["id"]
            item["criteria_id"] = notifier.criteria_id
            item["user_id"] = notifier.user_id
            img_list = item.get("img")
            if  img_list is not None and len(img_list) > 0:
                item["first_img_url"] = img_list[0]
            item.pop("img", None)
            item.pop("id", None)

            post_time = timeutils.convert_time(item["post_time"], timeutils.UTC_FORMT)
            if latest_notify_time is None or post_time > latest_notify_time:
                latest_notify_time = post_time # use get_latest post time as get_latest notify time

        if latest_notify_time is None:
            notifier.last_notify_time = self.current_notify_time # use current time as get_latest notify time
        else:
            notifier.last_notify_time = latest_notify_time

        return notify_items

    def next_query_post_time(self, notifier):
        last_notify_time = notifier.last_notify_time
        if constants.PRODUCT_MODE == False and constants.TEST_PERFORMANCE == True:
            return "2016-03-21T00:00:00Z"

        self.logger.info("next_query_post_time for user:" + notifier.user_id)

        if last_notify_time is None:
            self.logger.info("last_notify_time is None")
            time_string = timeutils.get_one_hour_ago(timeutils.UTC_FORMT)
            self.logger.info("next query post time is: "+ time_string)
            return time_string
        else:
            self.logger.info("last_notify_time is :" + timeutils.get_time_str(last_notify_time, timeutils.UTC_FORMT))

            if last_notify_time < self.current_query_post_time:
                self.logger.info("current_query_post_time is closer, using current_query_post_time")
                time_string = timeutils.get_time_str(self.current_query_post_time, timeutils.UTC_FORMT)
                self.logger.info("next query post time is: "+ time_string)
                return time_string
            else:
                self.logger.info("last_notify_time is closer, using last_notify_time")
                time_string = timeutils.get_time_str(last_notify_time, timeutils.UTC_FORMT)
                self.logger.info("next query post time is: "+ time_string)
                return timeutils.add_one_second(time_string, timeutils.UTC_FORMT)

    def get_query(self, notifier):
        query = {}
        query["query"] = "*:*"

        filters = {}
        filters["-parent"] = "*"
        input_filters = json_utils.load_json_str(notifier.filters, json_utils.UTF8_ENCODE)
        keys = input_filters.keys()
        for field in keys:
            field = str(field)
            filter_string = ""
            obj = input_filters.get(field)

            if field == "city":
                opt = "OR"
                city_filter_string = ""
                region_filter_string = ""
                cities = obj
                for city in cities:
                    regions = city.get("regions")
                    if regions is not None and len(regions) > 0:
                        for r in regions:
                            if region_filter_string == "":
                                region_filter_string =  "( " + str(r) + " "
                            else:
                                region_filter_string = region_filter_string + opt + " " + str(r) + " "

                    else:
                        city_code = city.get("code")
                        if city_filter_string == "":
                            city_filter_string =  "( " + str(city_code) + " "
                        else:
                            city_filter_string = city_filter_string + opt + " " + str(city_code) + " "

                if city_filter_string != "":
                    city_filter_string = city_filter_string+")"
                if region_filter_string != "":
                    region_filter_string = region_filter_string+")"

                if city_filter_string != "" and region_filter_string != "":
                    query["query"] = "city:" +city_filter_string + " "+opt+" region:" + region_filter_string
                elif city_filter_string != "":
                    filters["city"] = city_filter_string
                elif region_filter_string != "":
                    filters["region"] = region_filter_string

            elif isinstance(obj, str) or isinstance(obj, int) or isinstance(obj, bool):
                if field in self.nearby_fileds:
                    filter_string = "( * )"
                elif field == "basement":
                    filter_string = "{0 TO *}"
                    field = "floor"
                elif field == "shortest_lease":
                    filter_string = "[0 TO "+str(obj)+"]"
                else:
                    filter_string= str(obj)
            elif isinstance(obj, dict):
                if obj.get("from") is not None:
                    fromVal =  str(obj.get("from"))
                    toVal =  str(obj.get("to"))

                    if field == "price" or field == "size":
                        if toVal == "-1":
                            toVal = "*"
                        if fromVal == "-1":
                            fromVal = "*"
                    filter_string = "[ " +fromVal + " TO " + toVal +" ]"

                    if field == "price" and constants.PRODUCT_MODE == False and constants.TEST_PERFORMANCE == True:
                        filter_string = "[ " +str(self.price_seq) + " TO * ]"
                        self.price_seq = self.price_seq + 1

                elif obj.get("operator") is not None:
                    opt = str(obj.get("operator"))
                    values = obj.get("value")
                    filter_string = "("
                    for v in values:
                        if filter_string == "(":
                            filter_string = filter_string + " " + str(v) + " "
                        else:
                            filter_string = filter_string + opt + " " + str(v) + " "
                    filter_string = filter_string+")"

            if filter_string != "":
                if field == "city":
                    pass
                else:
                    filters[field] = filter_string

        query["filters"] = filters

        return query