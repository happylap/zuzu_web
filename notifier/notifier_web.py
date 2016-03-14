# encoding: utf-8

import requests
import json, logging
import LocalConstant
from notifier_model import Device, Criteria

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

    def getEffectiveCriteria(self):
        result = []
        resource  = "/criteria"

        response = self.get(resource)
        if response is None:
            return result
        criteria_list = response.get("data")
        if criteria_list is None or len(criteria_list) < 1:
            return result

        for data in criteria_list:
            criteria = Criteria(data)
            result.append(criteria)
        return result

    def getEnabledDevices(self):
        result = []
        resource  = "/device"
        devices = self.get(resource)
        if devices is None or len(devices) < 1:
            return result

        for data in devices:
            device = Device(data)
            if device.enabled == True:
                result.append(device)
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

