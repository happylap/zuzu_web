# encoding: utf-8

import logging

class NOTIFY_ERROR_TYPE(object):
    ERROR_QUERY_NOTIFY_ITEMS_EXCEPTION, \
    ERROR_SAVE_NOTIFY_ITEMS, \
    ERROR_ZUZU_WEB_EXCEPTION, \
    ERROR_SEND_NOTIFICATION, \
    ERROR_NO_DEVICES, \
    ERROR_NOTIFY_EXCEPTION, \
    ERROR_NO_NOTIFIER, \
    ERROR_PREPARE_DATA_EXCEPTION, \
    ERROR_MAIN_EXCEPTION, \
    = range(9)

class NotifyErrorStats(object):
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.stats = {}


    def add(self, error_type, user_id=None):
        if NOTIFY_ERROR_TYPE.ERROR_NO_DEVICES == error_type \
            or NOTIFY_ERROR_TYPE.ERROR_SAVE_NOTIFY_ITEMS == error_type \
            or NOTIFY_ERROR_TYPE.ERROR_QUERY_NOTIFY_ITEMS_EXCEPTION == error_type \
            or NOTIFY_ERROR_TYPE.ERROR_SEND_NOTIFICATION == error_type \
            or NOTIFY_ERROR_TYPE.ERROR_NOTIFY_EXCEPTION == error_type :
            error_notifiers = self.stats.get(error_type)
            if error_notifiers is None:
                error_notifiers = set()
            if user_id:
                error_notifiers.add(user_id)
            self.stats[error_type] = error_notifiers
        else:
            self.stats[error_type] = True
