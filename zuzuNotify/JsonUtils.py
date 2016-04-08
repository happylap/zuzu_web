# encoding: utf-8

'''
Created on 2015/8/24

@author: ted
'''

import json

#Open a json file with specified encoding
UTF8_ENCODE ="utf-8"

FILE_NOTIFIER_JSON = "notify.json"

def loadsJSONStr(s, encode=None):
    if encode is None:
        return json.loads(s)
    return json.loads(s, encode)

def loadJSON(filePath):
    with open(filePath, "r") as jsonFile:
        jsStr = jsonFile.read()
        js = json.loads(jsStr)
        jsonFile.close()
        return js

def updateJSON(filePath, data):
    with open(filePath, "w+") as jsonFile:
        jsonFile.write(json.dumps(data))
        jsonFile.close() 

def getNotifierJson():
    return loadJSON(FILE_NOTIFIER_JSON)

def updateNotifierJson(data):
    updateJSON(FILE_NOTIFIER_JSON, data)

