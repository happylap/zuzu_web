# encoding: utf-8

import logging

class NOTIFY_ERROR_TYPE(object):
    ERROR_NO_DEVICES, \
    ERROR_QUERY_NOTIFY_ITEMS_EXCEPTION, \
    ERROR_SAVE_NOTIFY_ITEMS, \
    ERROR_SEND_NOTIFICATION, \
    ERROR_NOTIFY_EXCEPTION, \
    ERROR_NO_NOTIFIER, \
    ERROR_PREPARE_DATA_EXCEPTION, \
    ERROR_MAIN_EXCEPTION, \
    = range(8)

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
            error_notifiers.add(user_id)
            self.stats[error_type] = error_notifiers
        else:
            self.stats[error_type] = True

    '''
    def export_errors(self):
        error_message_list = []
        for error in self.stats:
            error_message = ""
            error_notifiers = self.stats.get(error)
            if NOTIFY_ERROR_TYPE.ERROR_NO_DEVICES == error:
                error_message = "No devices found to notify following users: " + str(list(error_notifiers))
            elif NOTIFY_ERROR_TYPE.ERROR_SAVE_NOTIFY_ITEMS == error:
                error_message = "Saving notify items error for following users: " + str(list(error_notifiers))
            elif NOTIFY_ERROR_TYPE.ERROR_NOTIFY_EXCEPTION == error:
                error_message = "Unknown error while notifying following users: " + str(list(error_notifiers))
            elif NOTIFY_ERROR_TYPE.ERROR_NO_NOTIFIER == error:
                error_message = "There is no users found for notifications"
            elif NOTIFY_ERROR_TYPE.ERROR_MAIN_EXCEPTION == error:
                error_message = "Unknown error while prcessing the notify"
            if error_message != "":
                error_message_list.append(error_message)
        return error_message_list
    '''