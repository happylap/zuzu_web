# encoding: utf-8

import sys
import logging, datetime
import JsonUtils, TimeUtils, CommonUtils
from notifier_web import NotifierWeb
from notifier_solr import  NotifierSolr


class Notifier(object):
    def __init__(self):
        self.logger = CommonUtils.getLogger()
        self.json = JsonUtils.getNotifierJson()
        self.notifierWeb = NotifierWeb()
        self.rhc_solr_url = "http://ec2-52-76-69-228.ap-southeast-1.compute.amazonaws.com:8983/solr/rhc"
        self.notifier_solr_url = "http://ec2-52-77-238-225.ap-southeast-1.compute.amazonaws.com:8983/solr/newpost"
        self.rhcSolr = NotifierSolr(self.rhc_solr_url)
        self.notifierSolr = NotifierSolr(self.notifier_solr_url)

    def run(self):
        #newItems = self.getNewItems()
        #self.addItems(newItems)
        #self.updateLastPostTime()
        criteria_list = self.getCriteria()
        device_list = self.notifierWeb.getEnabledDevices()
        self.performQueryAndNotify(criteria_list, device_list)
        pass

    def getNewItems(self):
        new_items = self.rhcSolr.getNewPostItems(self.json["last_post_time"])
        if new_items is None or len(new_items) < 1:
            self.logger.info("No new items! Exit")
            sys.exit()
        return new_items

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
        new_time_String = TimeUtils.plusOneSecondAsString(self.json["last_post_time"])
        self.json["last_post_time"] = new_time_String
        JsonUtils.updateNotifierJson(self.json)

    def performQueryAndNotify(self, criteria_list, device_list):
        for criteria in criteria_list:
            filters = self.getFilters(criteria)
            query_post_time = self.getPostTimeForQuery(criteria)
            notify_items = self.getNotifyItems(query_post_time, filters)
            self.saveNotifyItems(notify_items)
            devices = self.getDevices(device_list, criteria.user_id)
            self.sendNotifications(notify_items, devices)

    def getNotifyItems(self, criteria):
        query_post_time = self.getNextQueryPostTime(criteria.last_notify_time)
        filters = criteria.getFilters()
        notify_items = self.notifierSolr.getNotifyItems(query_post_time, filters)
        return notify_items


    def getNextQueryPostTime(self, last_post_time):
        query_post_time = ""
        if last_post_time is None or last_post_time == "":
            query_post_time = TimeUtils.getOneHourAgoAsString(TimeUtils.UTC_FORMT)
        else:
            query_post_time = TimeUtils.plusOneSecondAsString(last_post_time, TimeUtils.UTC_FORMT)
        return query_post_time

    def getDevices(self, device_list, user_id):
        result = []
        for device in device_list:
            if user_id == device.user_id:
                result.append(device)
        return result

    def saveNotifyItems(self,notify_items):
        pass

    def sendNotifications(self,notify_items, devices):
        pass

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