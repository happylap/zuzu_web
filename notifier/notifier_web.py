# encoding: utf-8

import requests
import json, datetime
import CommonUtils, TimeUtils
from notifier_model import Device, Criteria

class NotifierWeb(object):
    def __init__(self):
        self.logger = CommonUtils.getLogger()
        #self.web_url = "http://ec2-52-76-69-228.ap-southeast-1.compute.amazonaws.com:8983/solr/rhc"
        self.web_url = "http://localhost:4567"

    def get(self, resource):
        try:
            r = requests.get(self.web_url+resource)
            if r.ok == True:
                js = json.loads(r.content)
                return js
            else:
                self.logger.error("Fail to get get resource: " + resource)
                return None
        except:
            self.logger.error("Exception while getting resource: " + resource)
            return None


    def getEffectiveCriteria(self):
        result = []
        resource  = "/criteria"
        list = self.get(resource)
        for data in list:
            criteria = Criteria(data)
            if criteria.enabled == False or criteria.user_id is None or criteria.expire_time is None:
                continue
            if TimeUtils.get_Now() > criteria.expire_time:
                continue
            result.append(criteria)

    def getEnabledDevices(self):
        result = []
        resource  = "/device"
        devices = self.get(resource)
        if devices is not None:
            for data in devices:
                device = Device(data)
                if device.enabled == True:
                    result.append(device)

        return result

