# encoding: utf-8

import sys
import logging, datetime
import JsonUtils, TimeUtils, LocalConstant
from notifier_web import NotifierWeb
from notifier_solr import NotifierSolr
from notifier_push import RHC_SNS


class NotifierService(object):
    PRODUCT_MODE = False
    nearby_fileds = ["nearby_train", "nearby_mrt", "nearby_bus", "nearby_thsr"]
    NOTIFY_ITEMS_LIMIT = 100
    TITLE_LENGTH = 15
    NOTIFY_SOUND = "bingbong.aiff"
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.json = JsonUtils.getNotifierJson()
        self.notifierWeb = NotifierWeb()
        self.rhc_solr_url = LocalConstant.RHC_SOLR_URL
        self.notifier_solr_url = LocalConstant.NOTIFIER_SOLR_URL
        self.rhcSolr = NotifierSolr(self.rhc_solr_url)
        self.notifierSolr = NotifierSolr(self.notifier_solr_url)
        self.sns = RHC_SNS()
        self.current_notify_time = TimeUtils.get_Now()
        self.current_query_post_tiem = TimeUtils.getHoursAgo(dt=self.current_notify_time, hours=1)

    def run(self):
        self.logger.info("current notify time: " + TimeUtils.getTimeString(self.current_notify_time, TimeUtils.UTC_FORMT))

        newItems = self.getNewItems()
        if newItems is not None and len(newItems) > 0:
            self.addItems(newItems)
            self.updateLastPostTime()

        notifier_list = self.notifierWeb.getNotifiers()
        if notifier_list is None or len(notifier_list) <=0:
            self.logger.info("no notifiers -> exit")
            sys.exit()

        for notifier in notifier_list:
            notify_items = self.getNotifyItems(notifier)
            if notify_items is None or len(notify_items) < 1:
                self.logger.info("no notify items for user: " + notifier.user_id)
                continue
            self.logger.info("find "+str(len(notify_items))+" notify items for user: " + notifier.user_id)
            self.notifierWeb.saveNotifyItems(notify_items)
            self.sendNotifications(notify_items, notifier)

    def getNewItems(self):
        self.logger.info("getNewItems after " + self.json["last_post_time"])
        return self.rhcSolr.getNewPostItems(self.json["last_post_time"])

    def addItems(self, newItems):
        if newItems is not None:
            self.logger.info("add " + str(len(newItems)) + " items to solr of notifier")
        else:
            self.logger.info("new item is None")
            return

        for item in newItems:
            try:
                item.pop("_version_", None)
                self.notifierSolr.add(item)
                self.notifierSolr.commit()
                self.json["last_post_time"] = item["post_time"]
            except:
                self.logger.error("Fail to add item: "+item["id"])

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
            self.updateNotifyTime(notifier)
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
                latest_notify_time = post_time # use latest post time as latest notify time

        if latest_notify_time is None:
            notifier.last_notify_time = self.current_notify_time # use current time as latest notify time
        else:
            notifier.last_notify_time = latest_notify_time
        self.updateNotifyTime(notifier)
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
                    filter_string = "{0 TO "+str(obj)+"}"
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
        invalid_device = []
        isSend = False
        for device in device_list:
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

            if len(invalid_device) > 0:
                self.notifierWeb.deleteDevices(invalid_device)

    def composeMessageBody(self, notify_items):
        item_size = len(notify_items)
        if item_size == 1:
            item = notify_items[0]
            price = str(item.get("price"))
            title = item.get("title")
            title = title[:self.TITLE_LENGTH]
            msg = u"一筆新刊登租屋符合您的需求\n租金:"+price+"\n"+u"標題:"+title
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
        if self.PRODUCT_MODE == True:
            message = {'APNS':apns_string}
        else:
            message = {'APNS_SANDBOX':apns_string}
        return JsonUtils.dumps(message, JsonUtils.UTF8_ENCODE)

def main():
    logname = "log/notifier"+"_%s.log" % datetime.datetime.utcnow().strftime("%Y%m%d_%H%M%S")
    logging.basicConfig(filename=logname,
                        filemode='wb',
                        format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                        datefmt='%H:%M:%S',
                        level=logging.INFO)

    notifier = NotifierService()
    notifier.run()

main()