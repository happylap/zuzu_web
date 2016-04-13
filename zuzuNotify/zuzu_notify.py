# encoding: utf-8

import sys
import json
import logging
import datetime
import asyncio
from zuzuNotify import JsonUtils, TimeUtils, LocalConstant
from zuzuNotify.zuzu_solr import SolrClient, AsyncSolrClient
from zuzuNotify.zuzu_web import ZuzuWeb, AsyncZuzuWeb
from zuzuNotify.zuzu_sns import SNSClient, AsyncSNSClient

class NotifyService(object):

    def __init__(self):
        self.logger = logging.getLogger(__name__)

        self.json = JsonUtils.getNotifierJson()

        # notification message
        self.TITLE_LENGTH = 30
        self.NOTIFY_SOUND = "bingbong.aiff"
        self.FULLWIDTH_COMMA= "，"

        #
        self.async_zuzu_web = AsyncZuzuWeb()
        self.async_notify_solr = AsyncSolrClient(LocalConstant.NOTIFIER_SOLR_URL)

        #
        self.endpoint_list = []

    def updateLastPostTime(self):
        self.logger.info("updateLastPostTime()...")
        new_time_String = TimeUtils.plusOneSecondAsString(self.json["last_post_time"], TimeUtils.UTC_FORMT)
        self.json["last_post_time"] = new_time_String
        self.logger.info("last_post_time becomes: " + self.json["last_post_time"])
        JsonUtils.updateNotifierJson(self.json)

    def prepareData(self):
        post_time = self.json["last_post_time"]
        self.logger.info("getNewItems after " + post_time)
        zuzu_solr = SolrClient(LocalConstant.ZUZU_SOLR_URL)
        new_items = zuzu_solr.getNewPostItems(post_time)
        if new_items is None or len(new_items) <= 0:
            return

        try:
            notify_solr = SolrClient(LocalConstant.NOTIFIER_SOLR_URL)
            for item in new_items:
                item.pop("_version_", None)
                notify_solr.add(item)
                self.json["last_post_time"] = item["post_time"]
            notify_solr.commit()
            self.updateLastPostTime()
        except:
            self.logger.error("Fail to add items")


    def startNotify(self):

        self.endpoint_list = SNSClient().getEnpoints()

        notifier_list = ZuzuWeb().getNotifiers()

        if notifier_list is None or len(notifier_list) <=0:
            self.logger.info("no notifiers -> exit")
            sys.exit()

        loop = asyncio.get_event_loop()
        self.async_sns_client = AsyncSNSClient(loop)

        coroutines = [self.doNotify(notifier,loop) for notifier in notifier_list]
        loop.run_until_complete(asyncio.wait(coroutines))
        loop.close()


    async def doNotify(self, notifier, loop):
        device_list = notifier.device_id
        if device_list is None or len(device_list) <=0:
            self.logger.error("no devices found for user: " + notifier.user_id)
            return

        device_list = notifier.device_id
        self.logger.info("device list:" + str(device_list))

        user_endpoint_list = []
        invalid_device =[]
        for device in device_list:
            endpoint = self.getEndpoints(device)
            if endpoint is not None:
                user_endpoint_list.append(endpoint)
            else:
                invalid_device.append(endpoint)

        if len(user_endpoint_list) <=0:
            self.logger.error("no valid snsendpoint found for user: " + notifier.user_id)
            return

        notify_items = await self.async_notify_solr.getNotifyItems(notifier)
        if notify_items is None or len(notify_items) < 1:
            self.logger.info("no zuzuNotify items for user: " + notifier.user_id)
            return

        is_save = await self.async_zuzu_web.saveNotifyItems(notify_items)
        if is_save == False:
            self.logger.error("save zuzuNotify items error, user:"+notifier.user_id)
            return


        await self.sendNotifications(notify_items, notifier, user_endpoint_list)

        await self.async_zuzu_web.updateNotifyTime(notifier)

        for device in invalid_device:
            await self.async_zuzu_web.deleteDevice(notifier.user_id, device)

        return


    def getEndpoints(self, device_id):
        for e in self.endpoint_list:
            if e.token is not None and e.token == device_id and e.enabled == True:
                return e
        return None

    async def sendNotifications(self,notify_items, notifier, user_endpoint_list):
        self.logger.info("sendNotifications()...")
        badge =  await self.async_zuzu_web.getUnreadNotifyItemNum(notifier.user_id)
        alert = self.composeMessageBody(notify_items)
        msg = self.composeAPNSMessage(alert, badge)
        self.logger.info("start to send notification for user: " + notifier.user_id)

        for endpoint in user_endpoint_list:
            try:
                self.logger.info("use endpoint: " + str(endpoint) +" to send notification")
                await self.async_sns_client.send(endpoint, msg, 'json')
            except:
                self.logger.error("Error while sending msg to user: "+notifier.user_id+" with endpoint: "+ endpoint)

    def composeMessageBody(self, notify_items):
        item_size = len(notify_items)
        if item_size == 1:
            try:
                item = notify_items[0]
                price = str(item.get("price"))
                title = item.get("title")

                if len(title) > self.TITLE_LENGTH:
                    title = title[:self.TITLE_LENGTH] + "..."
                msg = str(price)+"元"+self.FULLWIDTH_COMMA+title
            except:
                msg = str(item_size)+"筆新刊登租屋符合您的需求"
        else:
            msg = str(item_size)+"筆新刊登租屋符合您的需求"
        return msg

    def composeAPNSMessage(self, alert, badge):
        #body
        body = {}
        body["alert"] = alert
        body["badge"] = badge
        body["sound"] = self.NOTIFY_SOUND

        #apns dictionry
        apns_dict = {}
        apns_dict["aps"] = body
        apns_string = json.dumps(apns_dict, ensure_ascii=False)
        message = {'default':'default message',LocalConstant.APNS_MSG_HEADR:apns_string}
        messageJSON = json.dumps(message,ensure_ascii=False)
        return messageJSON

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

    notifier = NotifyService()
    notifier.prepareData()
    notifier.startNotify()

main()