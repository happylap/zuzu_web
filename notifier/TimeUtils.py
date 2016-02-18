# encoding: utf-8

import datetime

UTC_FORMT = "%Y-%m-%dT%H:%M:%SZ"

def get_Now():
    return datetime.datetime.utcnow()

def convertTime(time_string, format):
    try:
        return datetime.datetime.strptime(time_string, format)
    except:
        return None

def plusOneSecond(time_string, format):
    dt = datetime.datetime.strptime(time_string, format)
    return dt + datetime.timedelta(seconds=1)

def plusOneSecondAsString(time_string, format):
    dt = plusOneSecond(time_string, format)
    return dt.strftime(format)

def getOneHourAgo():
    dt = get_Now()+datetime.timedelta(hours=-1)

def getOneHourAgoAsString(format):
    dt = getOneHourAgo()
    return dt.strftime(format)

def getLatest(time_string_1, time_string_2, format):
    if time_string_1 is None or time_string_1 == "":
        return time_string_2
    elif time_string_2 is None or time_string_2 == "":
        return time_string_1

    dt1 = datetime.datetime.strptime(time_string_1, format)
    dt2 = datetime.datetime.strptime(time_string_2, format)
    if dt1 < dt2:
        return time_string_2
    else:
        return time_string_1