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
    from zuzuNotify import TimeUtils, JsonUtils, LocalConstant
else:
    import TimeUtils, JsonUtils, LocalConstant

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
        if results is not None:
            return results.results_count
        return 0

    def getNewPostItems(self, post_time):
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

    def deleteOldItems(self, post_time):
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

        self.current_notify_time = TimeUtils.get_Now()
        self.logger.info("current zuzuNotify time: " + TimeUtils.getTimeString(self.current_notify_time, TimeUtils.UTC_FORMT))

        if self.current_notify_time.hour == 0:
            self.NOTIFY_INTERVAL_HOURS = 8
            self.NOTIFY_ITEMS_LIMIT = 20
        else:
            self.NOTIFY_INTERVAL_HOURS = 1
            self.NOTIFY_ITEMS_LIMIT = 10

        self.current_query_post_tiem = TimeUtils.getHoursAgo(dt=self.current_notify_time, hours= self.NOTIFY_INTERVAL_HOURS)

        if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
            self.price_seq = 0

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

    async def getNotifyItems(self, notifier):
        self.logger.info("getNotifyItems for user: "+ notifier.user_id)

        query = self.getQuery(notifier)

        query_post_time = self.getNextQueryPostTime(notifier.last_notify_time)

        if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
            query_post_time = "2016-03-21T00:00:00Z"

        self.logger.info("query_post_time: " +str(query_post_time))

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

        self.logger.info("query criteria for "+notifier.user_id+": query=" + query["query"] + ", filters="+filters)


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

            post_time = TimeUtils.convertTime(item["post_time"],TimeUtils.UTC_FORMT)
            if latest_notify_time is None or post_time > latest_notify_time:
                latest_notify_time = post_time # use latest post time as latest zuzuNotify time

        if latest_notify_time is None:
            notifier.last_notify_time = self.current_notify_time # use current time as latest zuzuNotify time
        else:
            notifier.last_notify_time = latest_notify_time

        return notify_items

    def getNextQueryPostTime(self, last_notify_time):
        if last_notify_time is None:
            return TimeUtils.getOneHourAgoAsString(TimeUtils.UTC_FORMT)
        else:
            if last_notify_time < self.current_query_post_tiem:
                time_string = TimeUtils.getTimeString(self.current_query_post_tiem, TimeUtils.UTC_FORMT)
                return time_string
            else:
                time_string = TimeUtils.getTimeString(last_notify_time, TimeUtils.UTC_FORMT)
                return TimeUtils.plusOneSecondAsString(time_string, TimeUtils.UTC_FORMT)

    def getQuery(self, notifier):
        query = {}
        query["query"] = "*:*"

        filters = {}
        filters["-parent"] = "*"
        input_filters = JsonUtils.loadsJSONStr(notifier.filters, JsonUtils.UTF8_ENCODE)
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

                    if field == "price" and LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
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