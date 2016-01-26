# encoding: utf-8

import TimeUtils
import JsonUtils

class Device(object):
    def __init__(self, data):
        self.device_id = data.get("device_id")
        self.user_id = data.get("user_id")
        self.register_time = TimeUtils.convertTime(data.get("register_time"), TimeUtils.UTC_FORMT)
        if data.get("enabled") is not None and data.get("enabled") == True:
            self.enabled = True
        else:
            self.enabled = False



class Criteria(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.apple_product_id = data.get("apple_product_id")
        self.expire_time = TimeUtils.convertTime(data.get("expire_time"), TimeUtils.UTC_FORMT)
        self.last_notify_time = TimeUtils.convertTime(data.get("last_notify_time"), TimeUtils.UTC_FORMT)
        self.filters = data.get("filters").get("value")
        if data.get("enabled") is not None and data.get("enabled") == True:
            self.enabled = True
        else:
            self.enabled = False






