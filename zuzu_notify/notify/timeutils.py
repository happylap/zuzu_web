# encoding: utf-8

import datetime

UTC_FORMT = "%Y-%m-%dT%H:%M:%SZ"

def get_now(): # always get utc time
    return datetime.datetime.utcnow()

def get_hours_ago(dt, hours):
    return dt - datetime.timedelta(hours=hours)

def get_time_str(dt, format):
    return dt.strftime(format)

def convert_time(time_string, format):
    try:
        return datetime.datetime.strptime(time_string, format)
    except:
        return None

def add_one_second(time_string, format):
    dt = datetime.datetime.strptime(time_string, format)
    dt = dt + datetime.timedelta(seconds=1)
    return dt.strftime(format)

def get_one_hour_ago(format):
    dt = get_now() + datetime.timedelta(hours=-1)
    return dt.strftime(format)

def get_latest(time_string_1, time_string_2, format):
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