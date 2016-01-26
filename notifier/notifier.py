# encoding: utf-8

import sys
import logging, datetime
import JsonUtils, CommonUtils
from rhc_web import RHCWeb
from rhc_solr import  RHCSolr


class Notifier(object):
    def __init__(self):
        self.logger = CommonUtils.getLogger()
        self.json = JsonUtils.getNotifierJson()
        self.rhcWeb = RHCWeb()
        self.rhc_solr_url = "http://ec2-52-76-69-228.ap-southeast-1.compute.amazonaws.com:8983/solr/rhc"
        self.notifier_solr_url = "http://ec2-52-77-238-225.ap-southeast-1.compute.amazonaws.com:8983/solr/newpost"
        self.rhcSolr = RHCSolr(self.rhc_solr_url)
        self.notifierSolr = RHCSolr(self.notifier_solr_url)

    def run(self):
        #newItems = self.getNewItems()
        #self.addItems(newItems)
        devices = self.rhcWeb.getEnabledDevices()
        criteria = self.rhcWeb.getEffectiveCriteria()
        print devices
        print criteria
        pass

    def getNewItems(self):
        new_items = self.rhcSolr.getNewPostItems(self.json["last_post_time"])
        if new_items is None or len(new_items) < 1:
            self.logger.info("No new items! Exit")
            sys.exit()
        return new_items


    def addItems(self, newItems):
        for item in newItems:
            try:
                item.pop("_version_", None)
                self.json["last_post_time"] = item["post_time"]
                self.notifierSolr.add(item)
                self.notifierSolr.commit()
            except:
                self.logger.error("Fail to add item: "+item["id"])


    def sendNotifications(self):
        pass

    def composeQuery(self, criteria):
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