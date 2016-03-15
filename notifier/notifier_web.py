# encoding: utf-8

import requests
import json, logging
import LocalConstant, TimeUtils

class Notifier(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.device_id = data.get("device_id")
        self.last_notify_time = TimeUtils.convertTime(data.get("last_notify_time"), TimeUtils.UTC_FORMT)
        self.filters = data.get("filters")

class NotifierWeb(object):
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.web_url = LocalConstant.WEB_URL

    def get(self, resource):
        try:
            headers = {}
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            r = requests.get(self.web_url+resource, headers=headers)
            if r.ok == True:
                js = json.loads(r.content)
                return js
            else:
                self.logger.error("Fail to get resource: " + resource)
                return None
        except:
            self.logger.error("Exception while getting resource: " + resource)
            return None

    def post(self, resource, payload):
        try:
            headers = {}
            headers["Content-Type"] = "application/json"
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            data = json.dumps(payload)
            r = requests.post(self.web_url+resource, data=data, headers=headers)
            if r.ok == True:
                return True
            else:
                self.logger.error("Fail to post resource: " + resource +" , payload="+str(payload))
                return False
        except:
            self.logger.error("Exception while post resource: " + resource +" , payload="+str(payload))
            return False

    def patch_replace(self, resource, path, value):
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
                self.logger.error("Fail to patch resource: " + resource +" , path="+str(path) +" , value="+str(value))
                return False
        except:
            self.logger.error("Exception while patch resource: " + resource +" , path="+str(path)+" , value="+str(value))
            return False

    def delete(self, resource):
        try:
            headers = {}
            headers[LocalConstant.WEB_TOKEN_HEADER] = LocalConstant.WEB_TOKEN_VALUE
            r = requests.delete(self.web_url+resource, headers=headers)
            return r.ok
        except:
            self.logger.error("Exception while delete resource: " + resource)
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
        resource  = "/criteria/"+criteria_id+"/"+user_id
        path = "/lastNotifyTime"
        value = last_notify_time
        self.patch_replace(resource, path, value)

    def saveNotifyItems(self, notify_items):
        resource  = "/notifyitem/batch"
        payload = {"items":notify_items}
        self.post(resource, payload)

    def getUnreadNotifyItemNum(self, user_id):
        resource  = "/notifyitem/"+user_id
        items = self.get(resource)
        if items is None:
            return 0
        else:
            return len(items)

    def deleteDevices(self, user_id, deviceList):
        for device_id in deviceList:
            resource  = "/notifyitem/"+user_id+"/"+device_id
            if True == self.delete(resource):
                self.logger.indfo("delete token:"+device_id+", of user:"+user_id)
        pass


