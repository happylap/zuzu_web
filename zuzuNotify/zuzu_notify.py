# encoding: utf-8

import sys
import json
import logging
import time
import datetime
import asyncio

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1

if IS_LOCAL:
    from zuzuNotify import JsonUtils, TimeUtils, LocalConstant
    from zuzuNotify.zuzu_solr import SolrClient, AsyncSolrClient
    from zuzuNotify.zuzu_web import ZuzuWeb, AsyncZuzuWeb
    from zuzuNotify.zuzu_sns import SNSClient, AsyncSNSClient
    from zuzuNotify import zuzu_single_process
else:
    import JsonUtils, TimeUtils, LocalConstant
    from zuzu_solr import SolrClient, AsyncSolrClient
    from zuzu_web import ZuzuWeb, AsyncZuzuWeb
    from zuzu_sns import SNSClient, AsyncSNSClient
    import zuzu_single_process


class Timer(object):
    def __init__(self, verbose = False):
        self.verbose = verbose
        self.logger = logging.getLogger(__name__)

    def __enter__(self):
        self.start = time.time()
        return self

    def __exit__(self, *args):
        self.end = time.time()
        self.secs = self.end - self.start
        self.msecs = self.secs * 1000
        if self.verbose:
            self.logger.info('elapsed time: %f ms' % self.msecs)


class NotifyService(object):
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.json = JsonUtils.getNotifierJson()
        # notification message
        self.TITLE_LENGTH = 10
        self.NOTIFY_SOUND = "bingbong.aiff"
        self.FULLWIDTH_COMMA= "，"
        #
        self.async_notify_solr = AsyncSolrClient(LocalConstant.NOTIFIER_SOLR_URL)
        #
        self.endpoint_list = []
        #
        self.conn_limit = 20
        if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
            self.test_limit = 20000
            self.item_id_seq = 1


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

        new_list = []
        count = 0


        if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
            while(1):
                for n in notifier_list:
                    new_list.append(n)
                    count = count + 1
                    if count >= self.test_limit:
                        break
                if count >= self.test_limit:
                    break
            notifier_list = new_list


        if notifier_list is None or len(notifier_list) <=0:
            self.logger.info("no notifiers -> exit")
            raise SystemExit

        with Timer() as t:
            loop = asyncio.get_event_loop()
            self.async_sns_client = AsyncSNSClient(loop)
            self.async_zuzu_web = AsyncZuzuWeb(loop)
            loop.run_until_complete(self.notitfy(notifier_list))
            self.logger.info("close zuzuweb session")
            self.close(loop)
        print("elasped time: %s s" % t.secs)
        self.logger.info("elasped time: %s s" % t.secs)


    def close(self, loop):
        self.async_zuzu_web.close()
        self.async_sns_client.close()
        self.async_notify_solr.solr.close()
        loop.close()
        self.logger.info("loop.close()")


    async def notitfy(self, notifier_list):
        semaphore = asyncio.Semaphore(self.conn_limit)
        coroutines = [
            self.doNotify(semaphore, notifier)
            for notifier in notifier_list
        ]

        notitfy_results = await asyncio.gather(*coroutines)
        return notitfy_results

    async def doNotify(self, semaphore, notifier):
        with (await semaphore):
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

            if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
                for item in notify_items:
                    item["item_id"] = str(self.item_id_seq)
                    self.item_id_seq = self.item_id_seq + 1

            is_save = await self.async_zuzu_web.saveNotifyItems(notify_items)
            if is_save == False:
                self.logger.error("save zuzuNotify items error, user:"+notifier.user_id)
                return

            await self.async_zuzu_web.updateNotifyTime(notifier)

            await self.sendNotifications(notify_items, notifier, user_endpoint_list)

        #for device in invalid_device:
            #await self.async_zuzu_web.deleteDevice(notifier.user_id, device)

        return

    def getEndpoints(self, device_id):
        for e in self.endpoint_list:
            if e.token is not None and e.token == device_id:
                if LocalConstant.PRODUCT_MODE == False and LocalConstant.TEST_PERFORMANCE == True:
                    return e
                elif e.enabled == True:
                    return e
        return None

    async def sendNotifications(self,notify_items, notifier, user_endpoint_list):
        self.logger.info("sendNotifications()...")
        badge =  await self.async_zuzu_web.getUnreadNotifyItemNum(notifier.user_id)
        alert = self.composeMessageBody(notify_items)
        msg = self.composeAPNSMessage(alert, badge)
        self.logger.info("start to send notification for user: " + notifier.user_id)

        for endpoint in user_endpoint_list:
            self.logger.info("use endpoint: " + str(endpoint) +" to send notification")
            await self.async_sns_client.send(endpoint, msg, 'json')
            self.logger.info("message sent to endpoint "+ str(endpoint))


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

#@profile
def main():

    logname = LocalConstant.LOG_FOLDER+"/notifier"+"_%s.log" % datetime.datetime.utcnow().strftime("%Y%m%d_%H%M%S")

    logging.basicConfig(filename=logname,
                        filemode='w',
                        format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                        datefmt='%H:%M:%S',
                        level=logging.INFO)
    logger = logging.getLogger(__name__)

    try:
        zuzu_single_process.scriptStarter('no-force')

        notifier = NotifyService()
        notifier.prepareData()
        notifier.startNotify()
        zuzu_single_process.removePIDfile()
    except SystemExit:
        logger.error("SystemExit exception!!")
        zuzu_single_process.removePIDfile()
        sys.exit()
    except:
        logger.error("Unexpected error:", sys.exc_info())
        zuzu_single_process.removePIDfile()

if __name__=="__main__":
    main()