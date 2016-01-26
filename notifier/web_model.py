# encoding: utf-8

import CommonUtils
import JsonUtils

class Device(object):
    def __init__(self, data):
        self.device_id = data.get("device_id")
        self.user_id = data.get("user_id")
        self.register_time = CommonUtils.convertTime(data.get("register_time"))
        self.last_notify_time = CommonUtils.convertTime(data.get("last_notify_time"))
        if data.get("enabled") is not None:
            self.enabled = data.get("enabled")
        else:
            self.enabled = False



class Criteria(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.apple_product_id = data.get("apple_product_id")
        self.expire_time = CommonUtils.convertTime(data.get("expire_time"))
        self.qString = JsonUtils.loadsJSONStr(data.get("query_string"), JsonUtils.UTF8_ENCODE)
        if data.get("enabled") is not None:
            self.enabled = data.get("enabled")
        else:
            self.enabled = False

    def getFilters(self):
        filters = {}
        if self.qString is None:
            return filters

        single_value_fields = [""]


