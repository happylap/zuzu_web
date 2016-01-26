# encoding: utf-8

import logging
import datetime


RHC_WEB_TIME_FORMAT = "%Y-%m-%d %H:%M:%S"

def getLogger():
    return logging.getLogger('notifier')

def get_Now():
    return datetime.datetime.utcnow() + datetime.timedelta(hours=8)

def convertTime(time_string):
    try:
        return datetime.datetime.strptime(time_string, RHC_WEB_TIME_FORMAT)
    except:
        return None