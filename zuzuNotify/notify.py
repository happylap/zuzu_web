# encoding: utf-8

import sys
import logging
import datetime
import json
import requests
import asyncio
from zuzuNotify import JsonUtils, TimeUtils, LocalConstant
from zuzuNotify.solr import SolrClient


class Notifier(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.device_id = data.get("device_id")
        self.last_notify_time = TimeUtils.convertTime(data.get("last_notify_time"), TimeUtils.UTC_FORMT)
        self.filters = data.get("filters")

class Notify(object):

    def __init__(self):
        self.logger = logging.getLogger(__name__)

        self.json = JsonUtils.getNotifierJson()

        self.NOTIFY_INTERVAL_HOURS = 1
        self.NOTIFY_ITEMS_LIMIT = 5
        self.TITLE_LENGTH = 30
        self.NOTIFY_SOUND = "bingbong.aiff"
        self.FULLWIDTH_COMMA= "，"

        self.nearby_fileds = ["nearby_train", "nearby_mrt", "nearby_bus", "nearby_thsr"]

        self.current_notify_time = TimeUtils.get_Now()

        if self.current_notify_time.hour == 0:
            self.NOTIFY_INTERVAL_HOURS = 8
            self.NOTIFY_ITEMS_LIMIT = 10
        else:
            pass

        self.current_query_post_tiem = TimeUtils.getHoursAgo(dt=self.current_notify_time, hours= self.NOTIFY_INTERVAL_HOURS)

    def prepareData(self):
        post_time = self.json["last_post_time"]
        self.logger.info("getNewItems after " + post_time)
        zuzuSolr = SolrClient(LocalConstant.ZUZU_SOLR_URL)
        new_items = zuzuSolr.getNewPostItems(post_time)
        if new_items is None or len(new_items) <= 0:
            return

        notify_solr = SolrClient(LocalConstant.NOTIFIER_SOLR_URL)
        try:
            for item in new_items:
                item.pop("_version_", None)
                notify_solr.add(item)
                self.json["last_post_time"] = item["post_time"]
            notify_solr.commit()
        except:
            self.logger.error("Fail to add items")


    def _get(self, resource):
        self.logger.info("get "+str(resource))
        try:
            headers = {}
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            r = requests.get(LocalConstant.WEB_URL+resource, headers=headers)
            if r.ok == True:
                return r.json()
            else:
                self.logger.error("Fail to get resource: " + str(resource))
                return None
        except:
            self.logger.error("Exception while getting resource: " + str(resource))
            return None

    def getNotifiers(self):
        result = []
        resource  = "/notifier"
        response = self._get(resource)
        if response is None:
            return result
        notifier_list = response.get("data")
        if notifier_list is None or len(notifier_list) < 1:
            return result

        for data in notifier_list:
            notifier = Notifier(data)
            result.append(notifier)
        return result

    async def doNotify(self, notifier):
        pass

    def startNotify(self):
        self.logger.info("current zuzuNotify time: " + TimeUtils.getTimeString(self.current_notify_time, TimeUtils.UTC_FORMT))

        notifier_list = self.getNotifiers()
        if notifier_list is None or len(notifier_list) <=0:
            self.logger.info("no notifiers -> exit")
            sys.exit()

        loop = asyncio.get_event_loop()
        coroutines = [self.doNotify(notifier) for notifier in notifier_list]
        loop.run_until_complete(asyncio.wait(coroutines))
        loop.close()

        for notifier in notifier_list:
            try:
                notify_items = self.getNotifyItems(notifier)

                if notify_items is None or len(notify_items) < 1:
                    self.logger.info("no zuzuNotify items for user: " + notifier.user_id)
                    continue

                self.logger.info("find "+str(len(notify_items))+" zuzuNotify items for user: " + notifier.user_id)

                is_save = self.notifierWeb.saveNotifyItems(notify_items)

                if is_save == False:
                    self.logger.error("save zuzuNotify items error, user:"+notifier.user_id+", current_query_post_tiem="+str(self.current_query_post_tiem))
                    continue

                self.updateNotifyTime(notifier)

                self.sendNotifications(notify_items, notifier)
            except:
                self.logger.error("Exception while process the notifier of user: "+notifier.user_id)


    def updateLastPostTime(self):
        self.logger.info("updateLastPostTime()...")
        new_time_String = TimeUtils.plusOneSecondAsString(self.json["last_post_time"], TimeUtils.UTC_FORMT)
        self.json["last_post_time"] = new_time_String
        self.logger.info("last_post_time becomes: " + self.json["last_post_time"])
        JsonUtils.updateNotifierJson(self.json)


    def getNotifyItems(self, notifier):
        self.logger.info("getNotifyItems for user: "+ notifier.user_id)
        query_post_time = self.getNextQueryPostTime(notifier.last_notify_time)

        self.logger.info("query_post_time: " +str(query_post_time))
        query = self.getQuery(notifier)
        notify_items = self.notifierSolr.getNotifyItems(query["query"],query["filters"], query_post_time)
        if notify_items is None or len(notify_items) < 1:
            notifier.last_notify_time = self.current_notify_time
            return notify_items

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
        return notify_items[:self.NOTIFY_ITEMS_LIMIT]

    def updateNotifyTime(self, notifier):
        self.logger.info("updateNotifyTime()...")
        criteria_id = notifier.criteria_id
        user_id = notifier.user_id
        last_notify_time = notifier.last_notify_time
        last_notify_time_str = TimeUtils.getTimeString(last_notify_time, TimeUtils.UTC_FORMT)
        self.logger.info("last_notify_time becomes: " + last_notify_time_str)
        self.notifierWeb.updateCriteriaLastNotifyTime(criteria_id, user_id, last_notify_time_str)

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

            elif isinstance(obj, unicode) or isinstance(obj, str) or isinstance(obj, int) or isinstance(obj, bool):
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

    def sendNotifications(self,notify_items, notifier):
        self.logger.info("sendNotifications()...")
        badge = self.notifierWeb.getUnreadNotifyItemNum(notifier.user_id)
        alert = self.composeMessageBody(notify_items)
        msg = self.composeAPNSMessage(alert, badge)
        self.logger.info("start to send notification for user: " + notifier.user_id)
        device_list = notifier.device_id
        self.logger.info("device list:" + str(device_list))
        invalid_device = []
        isSend = False
        for device in device_list:
            try:
                endpoint = self.sns.getEndpoints(device)
                if endpoint is not None:
                    isSend = True
                    self.logger.info("use device: " + str(device) +" to send notification")
                    self.sns.send(endpoint, msg, 'json')
                else:
                    self.logger.info("found invalid device: " + str(device))
                    invalid_device.append(device)

                if isSend == False:
                    self.logger.info("Cannot find any device to send for user: " + notifier.user_id)

                if len(invalid_device) > 0 and LocalConstant.PRODUCT_MODE == True:
                    self.notifierWeb.deleteDevices(notifier.user_id, invalid_device)
            except:
                pass

    def composeMessageBody(self, notify_items):
        item_size = len(notify_items)
        if item_size == 1:
            try:
                item = notify_items[0]
                price = str(item.get("price"))
                title = item.get("title")

                if len(title) > self.TITLE_LENGTH:
                    title = title[:self.TITLE_LENGTH] + "..."
                msg = str(price)+u"元"+self.FULLWIDTH_COMMA+title
            except:
                msg = str(item_size)+u"筆新刊登租屋符合您的需求"
        else:
            msg = str(item_size)+u"筆新刊登租屋符合您的需求"
        return msg

    def composeAPNSMessage(self, alert, badge):
        apns_dict = {}
        body = {}
        body["alert"] = alert
        body["badge"] = badge
        body["sound"] = self.NOTIFY_SOUND
        apns_dict["aps"] = body
        apns_string = JsonUtils.dumps(apns_dict,JsonUtils.UTF8_ENCODE)
        message = {LocalConstant.APNS_MSG_HEADR:apns_string}
        return JsonUtils.dumps(message, JsonUtils.UTF8_ENCODE)

def main():

    logname = LocalConstant.LOG_FOLDER+"/notifier"+"_%s.log" % datetime.datetime.utcnow().strftime("%Y%m%d_%H%M%S")

    logging.basicConfig(filename=logname,
                        filemode='w',
                        format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                        datefmt='%H:%M:%S',
                        level=logging.INFO)

    '''
    logger = logging.getLogger(__name__)

    try:
        singleton("notifier_service")
    except:
        logger.error("notifier_service process is already running...")
        sys.exit()
    '''

    notifier = Notify()
    notifier.prepareData()
    notifier.startNotify()

main()