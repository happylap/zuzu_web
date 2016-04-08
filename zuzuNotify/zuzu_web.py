# encoding: utf-8

import requests
import json, logging
from zuzuNotify import LocalConstant, TimeUtils

class Notifier(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.device_id = data.get("device_id")
        self.last_notify_time = TimeUtils.convertTime(data.get("last_notify_time"), TimeUtils.UTC_FORMT)
        self.filters = data.get("filters")

class ZuzuWeb(object):
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.web_url = LocalConstant.WEB_URL

    def get(self, resource):
        self.logger.info("get "+str(resource))
        try:
            headers = {}
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            r = requests.get(self.web_url+resource, headers=headers)
            if r.ok == True:
                js = json.loads(r.content)
                return js
            else:
                self.logger.error("Fail to get resource: " + str(resource))
                return None
        except:
            self.logger.error("Exception while getting resource: " + str(resource))
            return None

    def post(self, resource, payload):
        self.logger.info("post "+str(resource))
        try:
            headers = {}
            headers["Content-Type"] = "application/json"
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            data = json.dumps(payload)
            r = requests.post(self.web_url+resource, data=data, headers=headers)
            if r.ok == True:
                js = json.loads(r.content)
                if js.get("code") is not None and js.get("code") == 200:
                    return True
                else:
                    error_msg = js.get("message")
                    if error_msg is not None:
                        self.logger.error("save zuzuNotify items error: "+error_msg)
                    return False
            else:
                self.logger.error("Fail to post resource: " + resource +" , payload="+str(payload))
                return False
        except:
            self.logger.error("Exception while post resource: " + resource +" , payload="+str(payload))
            return False

    def patch_replace(self, resource, path, value):
        self.logger.info("post "+str(resource)+", path:"+ str(path)+", value:"+str(value))
        try:
            headers = {}
            headers["Content-Type"] = "application/json"
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            payload = {}
            payload["op"] = "replace"
            payload["path"] = path
            payload["value"] = value
            patch_list = [payload]
            data = json.dumps(patch_list)
            r = requests.patch(self.web_url+resource, data=data, headers=headers)
            if r.ok == True:
                return True
            else:
                self.logger.error("Fail to patch resource: " + str(resource) +" , path="+str(path) +" , value="+str(value))
                return False
        except:
            self.logger.error("Exception while patch resource: " + str(resource) +" , path="+str(path)+" , value="+str(value))
            return False

    def delete(self, resource):
        self.logger.info("delete "+str(resource))
        try:
            headers = {}
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            r = requests.delete(self.web_url+resource, headers=headers)
            return r.ok
        except:
            self.logger.error("Exception while delete resource: " + str(resource))
            return False

    def getNotifiers(self):
        result = []
        resource  = "/notifier"
        response = self.get(resource)
        if response is None:
            return result
        notifier_list = response.get("data")
        if notifier_list is None or len(notifier_list) < 1:
            return result

        for data in notifier_list:
            notifier = Notifier(data)
            result.append(notifier)
        return result


    def updateCriteriaLastNotifyTime(self, criteria_id, user_id, last_notify_time):
        resource  = "/criteria/"+user_id+"/"+criteria_id
        path = "/lastNotifyTime"
        value = last_notify_time
        self.patch_replace(resource, path, value)

    def saveNotifyItems(self, notify_items):
        resource  = "/notifyitem/batch"
        payload = {"items":notify_items}
        return self.post(resource, payload)

    def getUnreadNotifyItemNum(self, user_id):
        resource  = "/notifyitem/unreadcount/"+user_id
        items = self.get(resource)
        if items.get("data") is not None:
            return items.get("data")
        else:
            return 0

    def deleteDevices(self, user_id, deviceList):
        for device_id in deviceList:
            resource  = "/device/"+user_id+"/"+device_id
            if True == self.delete(resource):
                self.logger.info("delete token: "+str(device_id)+", of user:"+str(user_id))
            else:
                self.logger.error("delete token error: "+str(device_id)+", of user:"+str(user_id))
        pass


