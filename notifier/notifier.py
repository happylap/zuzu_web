# encoding: utf-8

import sys
import logging, datetime
import JsonUtils, TimeUtils, CommonUtils
from notifier_web import NotifierWeb
from notifier_solr import  NotifierSolr


class Notifier(object):

    nearby_fileds = ["nearby_train", "nearby_mrt", "nearby_bus", "nearby_thsr"]

    def __init__(self):
        self.logger = CommonUtils.getLogger()
        self.json = JsonUtils.getNotifierJson()
        self.notifierWeb = NotifierWeb()
        self.rhc_solr_url = "http://ec2-52-76-69-228.ap-southeast-1.compute.amazonaws.com:8983/solr/rhc"
        self.notifier_solr_url = "http://ec2-52-77-238-225.ap-southeast-1.compute.amazonaws.com:8983/solr/newpost"
        self.rhcSolr = NotifierSolr(self.rhc_solr_url)
        self.notifierSolr = NotifierSolr(self.notifier_solr_url)

    def run(self):
        newItems = self.getNewItems()
        if newItems is not None and len(newItems) > 0:
            self.addItems(newItems)
        self.updateLastPostTime()
        criteria_list = self.getCriteria()
        device_list = self.notifierWeb.getEnabledDevices()
        self.performQueryAndNotify(criteria_list, device_list)
        pass

    def getNewItems(self):
        return self.rhcSolr.getNewPostItems(self.json["last_post_time"])

    def getCriteria(self):
        criteria = self.notifierWeb.getEffectiveCriteria()
        if criteria is None or len(criteria) < 1:
            self.logger.info("No criteria! Exit")
            sys.exit()
        return criteria

    def addItems(self, newItems):
        for item in newItems:
            try:
                item.pop("_version_", None)
                self.notifierSolr.add(item)
                self.notifierSolr.commit()
                self.json["last_post_time"] = item["post_time"]
            except:
                self.logger.error("Fail to add item: "+item["id"])

    def updateLastPostTime(self):
        new_time_String = TimeUtils.plusOneSecondAsString(self.json["last_post_time"], TimeUtils.UTC_FORMT)
        self.json["last_post_time"] = new_time_String
        JsonUtils.updateNotifierJson(self.json)

    def performQueryAndNotify(self, criteria_list, device_list):
        for criteria in criteria_list:
            notify_items = self.getNotifyItems(criteria)
            if notify_items is None or len(notify_items) < 1:
                continue
            self.notifierWeb.saveNotifyItems(notify_items)
            devices = self.getDevices(device_list, criteria.user_id)
            self.sendNotifications(notify_items, devices)


    def getNotifyItems(self, criteria):
        query_post_time = self.getNextQueryPostTime(criteria.last_notify_time)
        query = self.getQuery(criteria)
        notify_items = self.notifierSolr.getNotifyItems(query["query"],query["filters"], query_post_time)
        if notify_items is None or len(notify_items) < 1:
            return notify_items

        for item in notify_items:
            item["item_id"] = item["id"]
            item["criteria_id"] = criteria.criteria_id
            item["user_id"] = criteria.user_id
            img_list = item.get("img")
            if  img_list is not None and len(img_list) > 0:
                item["first_img_url"] = img_list[0]
            item.pop("img", None)
            item.pop("id", None)
            criteria.last_notify_time = item["post_time"]
        self.updateNotifyTime(criteria)
        return notify_items

    def updateNotifyTime(self, criteria):
        criteria_id = criteria.criteria_id
        user_id = criteria.user_id
        last_notify_time = criteria.last_notify_time
        self.notifierWeb.updateCriteriaLastNotifyTime(criteria_id, user_id, last_notify_time)

    def getNextQueryPostTime(self, last_post_time):
        query_post_time = ""
        if last_post_time is None:
            query_post_time = TimeUtils.getOneHourAgoAsString(TimeUtils.UTC_FORMT)
        else:
            time_string = last_post_time.strftime(TimeUtils.UTC_FORMT)
            query_post_time = TimeUtils.plusOneSecondAsString(time_string, TimeUtils.UTC_FORMT)
        return query_post_time

    def getDevices(self, device_list, user_id):
        result = []
        for device in device_list:
            if user_id == device.user_id:
                result.append(device)
        return result

    def sendNotifications(self,notify_items, devices):
        pass

    def getQuery(self, criteria):
        query = {}
        filters = {}
        input_filters = JsonUtils.loadsJSONStr(criteria.filters, JsonUtils.UTF8_ENCODE)
        keys = input_filters.keys()
        for field in keys:
            field = str(field)
            filter_string = ""
            obj = input_filters.get(field)
            if isinstance(obj, unicode) or isinstance(obj, str) or isinstance(obj, int) or isinstance(obj, bool):
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
                if field == "region" or field == "city":
                    query[field] = filter_string
                else:
                    filters[field] = filter_string

        query["filters"] = filters

        if query["city"] is not None and  query["region"] is not None:
            query["query"] = "city:"+query["city"]+" OR " + "region:"+query["region"]
        elif query["city"] is not None:
            query["query"] = "city:"+query["city"]
        elif query["region"] is not None:
            query["query"] = "region:"+query["region"]

        return query

def main():
    logname = "log/notifier"+"_%s.log" % datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    logging.basicConfig(filename=logname,
                        filemode='wb',
                        format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                        datefmt='%H:%M:%S',
                        level=logging.INFO)

    notifier = Notifier()
    notifier.run()

main()