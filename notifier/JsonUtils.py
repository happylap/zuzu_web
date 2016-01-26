# encoding: utf-8

'''
Created on 2015/8/24

@author: ted
'''

import json

#Open a json file with specified encoding
UTF8_ENCODE ="utf-8"

FILE_NOTIFIER_JSON = "notifier.json"

def loadsJSONStr(s, encode=None):
    if encode is None:
        return json.loads(s)
    return json.loads(s, encode)

def loadJSON(filePath, fileEncoding):
    with open(filePath, "r") as jsonFile:
        jsStr = jsonFile.read().decode(fileEncoding)
        js = json.loads(jsStr)
        jsonFile.close()
        return js

def updateJSON(filePath, data, fileEncoding):
    with open(filePath, "w+") as jsonFile:
        jsonFile.write(json.dumps(data).encode(fileEncoding))
        jsonFile.close() 

def getNotifierJson():
    return loadJSON(FILE_NOTIFIER_JSON, UTF8_ENCODE)

def updateNotifierJson(data):
    updateJSON(FILE_NOTIFIER_JSON, data, UTF8_ENCODE)

