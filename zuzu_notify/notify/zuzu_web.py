# encoding: utf-8

import sys
import logging
import json
import requests
import aiohttp

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1

if IS_LOCAL:
    from notify import constants
    from notify import timeutils, error_stats
else:
    import constants, timeutils, error_stats

class Notifier(object):
    def __init__(self, data):
        self.criteria_id = data.get("criteria_id")
        self.user_id = data.get("user_id")
        self.device_id = data.get("device_id")
        self.last_notify_time = timeutils.convert_time(data.get("last_notify_time"), timeutils.UTC_FORMT)
        self.filters = data.get("filters")
        self.notify_enabled = data.get("enabled")

class ZuzuWeb(object):
    def __init__(self, notify_error_stats):
        self.notify_error_stats = notify_error_stats
        self.logger = logging.getLogger(__name__)
        self.web_url = constants.WEB_URL

    def get(self, resource):
        self.logger.info("get "+str(resource))
        try:
            headers = {}
            headers[constants.WEB_TOKEN_HEADER] = constants.WEB_TOKEN_VALUE
            r = requests.get(self.web_url+resource, headers=headers)
            if r.ok == True:
                js = r.json()
                return js
            else:
                self.logger.error("Fail to get resource: " + str(resource))
                return None
        except:
            self.logger.error("Exception while getting resource: " + str(resource))
            self.logger.error("Error message:"+str(sys.exc_info()))
            self.notify_error_stats.add(error_type=error_stats.NOTIFY_ERROR_TYPE.ERROR_ZUZU_WEB_EXCEPTION)
            return None

    def get_notifier_list(self):
        result = []
        resource  = "/notifier"
        response = self.get(resource)
        if response is None:
            return result
        notifier_list = response.get("data")
        if notifier_list is None or len(notifier_list) < 1:
            return result

        for data in notifier_list:
            notifier = Notifier(data)
            result.append(notifier)
        return result


class AsyncZuzuWeb(object):

    def __init__(self, loop, notify_error_stats):
        self.logger = logging.getLogger(__name__)
        self.notify_error_stats = notify_error_stats
        self.web_url = constants.WEB_URL
        self.session = aiohttp.ClientSession(loop=loop)

    async def get(self, resource):
        self.logger.info("get "+str(resource))
        try:
            headers = {}
            headers[constants.WEB_TOKEN_HEADER] = constants.WEB_TOKEN_VALUE
            response = await self.session.get(self.web_url+resource, headers=headers)
            return response
        except:
            self.logger.error("Exception while getting resource: " + str(resource))
            self.logger.error("Error message:"+str(sys.exc_info()))
            self.notify_error_stats.add(error_type=error_stats.NOTIFY_ERROR_TYPE.ERROR_ZUZU_WEB_EXCEPTION)
            return None

    async def delete(self, resource):
        self.logger.info("delete "+str(resource))
        try:
            headers = {}
            headers[constants.WEB_TOKEN_HEADER] = constants.WEB_TOKEN_VALUE
            r = await self.session.delete(self.web_url+resource, headers=headers)

            await r.release()
            if r.status == 200:
                return True
            return False
        except:
            self.logger.error("Exception while delete resource: " + str(resource))
            self.logger.error("Error message:"+str(sys.exc_info()))
            self.notify_error_stats.add(error_type=error_stats.NOTIFY_ERROR_TYPE.ERROR_ZUZU_WEB_EXCEPTION)
            return False

    async def post(self, resource, payload):
        self.logger.info("post "+str(resource))
        try:
            headers = {}
            headers["Content-Type"] = "application/json"
            headers[constants.WEB_TOKEN_HEADER] = constants.WEB_TOKEN_VALUE
            data = json.dumps(payload)

            r = await self.session.post(self.web_url+resource, data=data, headers=headers)

            js = await r.json()

            await r.release()

            if js.get("code") is not None and js.get("code") == 200:
                return True
            else:
                error_msg = js.get("message")
                if error_msg is not None:
                    self.logger.error(error_msg)
                return False
        except:
            self.logger.error("Exception while post resource: " + resource +" , payload="+str(payload))
            self.logger.error("Error message:"+str(sys.exc_info()))
            self.notify_error_stats.add(error_type=error_stats.NOTIFY_ERROR_TYPE.ERROR_ZUZU_WEB_EXCEPTION)
            return False


    async def patch_replace(self, resource, path, value):
        self.logger.info("patch "+str(resource)+", path:"+ str(path)+", value:"+str(value))
        try:
            headers = {}
            headers["Content-Type"] = "application/json"
            headers[constants.WEB_TOKEN_HEADER] = constants.WEB_TOKEN_VALUE
            payload = {}
            payload["op"] = "replace"
            payload["path"] = path
            payload["value"] = value
            patch_list = [payload]
            data = json.dumps(patch_list)

            r = await self.session.patch(self.web_url+resource, data=data, headers=headers)

            js = await r.json()

            await r.release()

            if js.get("code") is not None and js.get("code") == 200:
                return True
            else:
                self.logger.error("Fail to patch resource: " + str(resource) +" , path="+str(path) +" , value="+str(value))
                error_msg = js.get("message")
                if error_msg is not None:
                    self.logger.error(error_msg)
                return False
        except:
            self.logger.error("Exception while patch resource: " + str(resource) +" , path="+str(path)+" , value="+str(value))
            return False

    async def save_notify_items(self, notify_items):
        resource  = "/notifyitem/batch"
        payload = {"items":notify_items}
        result = await self.post(resource, payload)
        return result


    async def update_last_notify_time(self, notifier):
        self.logger.info("update_last_notify_time()...")
        criteria_id = notifier.criteria_id
        user_id = notifier.user_id
        last_notify_time_str = timeutils.get_time_str(notifier.last_notify_time, timeutils.UTC_FORMT)

        resource  = "/criteria/"+user_id+"/"+criteria_id
        path = "/lastNotifyTime"
        value = last_notify_time_str
        await self.patch_replace(resource, path, value)

    async def get_latest_receive_count(self, user_id):
        resource  = "/notifyitem/latestreceivecount/"+user_id
        response = await self.get(resource)
        js = await response.json()

        await response.release()

        data_size = js.get("data")
        if data_size is not None:
            return data_size
        return 0

    async def delete_device(self, user_id, device_id):
        resource  = "/device/"+user_id+"/"+device_id
        if True == await self.delete(resource):
            self.logger.info("delete token: "+str(device_id)+", of user:"+str(user_id))
        else:
            self.logger.error("delete token error: "+str(device_id)+", of user:"+str(user_id))

    async def delete_device_list(self, user_id, deviceList):
        for device_id in deviceList:
            resource  = "/device/"+user_id+"/"+device_id
            if True == await self.delete(resource):
                self.logger.info("delete token: "+str(device_id)+", of user:"+str(user_id))
            else:
                self.logger.error("delete token error: "+str(device_id)+", of user:"+str(user_id))
        pass

    def close(self):
        self.session.close()
