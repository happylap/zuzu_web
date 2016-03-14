# encoding: utf-8

import TimeUtils

class Device(object):
    def __init__(self, data):
        self.device_id = data.get("device_id")
        self.user_id = data.get("user_id")
        self.register_time = TimeUtils.convertTime(data.get("register_time"), TimeUtils.UTC_FORMT)
        if data.get("enabled") is not None and data.get("enabled") == True:
            self.enabled = True
        else:
            self.enabled = False











