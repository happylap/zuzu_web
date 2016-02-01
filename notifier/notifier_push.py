# encoding: utf-8

import boto
import boto.exception
import boto.sns
import pprint
import re
import json

class RHC_Enpoint(object):
    def __init__(self, endpoint_arn=None, enabled=None, user_data=None):
        self.arn = endpoint_arn
        if 'true' == enabled:
            self.enabled = True
        else:
            self.enabled = False
        if user_data is not None:
            self.user_data = user_data
        else:
            self.user_data = None


class RHC_SNS(object):
    
    def __init__(self):
        region = [r for r in boto.sns.regions() if r.name==u'ap-northeast-1'][0]
        self.sns = boto.sns.SNSConnection(
            aws_access_key_id="AKIAJ767YDIFH276KCIQ",
            aws_secret_access_key="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",
            region=region,
        )
        self.endpoint_list = self.initEnpoints()

    def initEnpoints(self):
        endpoint_list = []
        try:
            response = self.sns.list_endpoints_by_platform_application("arn:aws:sns:ap-northeast-1:994273935857:app/APNS_SANDBOX/zuzurentals_development")
            res = response['ListEndpointsByPlatformApplicationResponse']
            result = res['ListEndpointsByPlatformApplicationResult']
            endpoints = result['Endpoints']
            for e in endpoints:
                arn = e.get('EndpointArn')
                enable = e.get('Attributes').get('Enabled')
                user_data = e.get('Attributes').get('CustomUserData')
                endpoint_list.append(RHC_Enpoint(arn,enable,user_data))
        except:
            pass
        return endpoint_list

    def getEndpoints(self, user_id):
        result = []
        for e in self.endpoint_list:
            if e.user_data is not None and e.user_data == user_id and e.enabled == True:
                result.append(e)
        return result

    def send(self, endpoint, msg):
        publish_result = self.sns.publish(target_arn=endpoint.arn, message=msg)
        pprint.pprint(publish_result)

    def sendAll(self, msg):
        endpoint_list = self.endpoint_list
        for endpoint in endpoint_list:
            endpoint_arn= endpoint.arn
            if endpoint_arn is not None and endpoint.enabled:
                print "ARN:", endpoint_arn
                publish_result = self.sns.publish(target_arn=endpoint_arn, message=msg)
                pprint.pprint(publish_result)

'''
zuzu = RHC_SNS()
text = u"這是測試中文的長度!這是測試中文的長度!這是測試中文的長度!這是測試中文的長度!這是測試中文的長度!這是測試中文的長度!這是測試中文的長度!"
#text2 = "777"
zuzu.sendAll(text)
'''
