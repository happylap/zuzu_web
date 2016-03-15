# encoding: utf-8

import boto
import boto.exception
import boto.sns
import pprint
import LocalConstant


class RHC_Enpoint(object):
    def __init__(self, endpoint_arn=None, enabled=None, token=None):
        self.arn = endpoint_arn
        if 'true' == enabled:
            self.enabled = True
        else:
            self.enabled = False
        if token is not None:
            self.token = token
        else:
            self.token = None


class RHC_SNS(object):
    
    def __init__(self):
        region = [r for r in boto.sns.regions() if r.name==u'ap-northeast-1'][0]
        self.sns = boto.sns.SNSConnection(
            aws_access_key_id = LocalConstant.AWS_ACCESS_KEY_ID,
            aws_secret_access_key = LocalConstant.AWS_SECRET_ACCESS_KEY,
            region=region,
        )
        self.endpoint_list = self.initEnpoints()

    def initEnpoints(self):
        endpoint_list = []
        try:
            response = self.sns.list_endpoints_by_platform_application(LocalConstant.SNS_PLATFORM)
            res = response['ListEndpointsByPlatformApplicationResponse']
            result = res['ListEndpointsByPlatformApplicationResult']
            endpoints = result['Endpoints']
            for e in endpoints:
                arn = e.get('EndpointArn')
                enable = e.get('Attributes').get('Enabled')
                token = e.get('Attributes').get('Token')
                endpoint_list.append(RHC_Enpoint(arn,enable,token))
        except:
            pass
        return endpoint_list

    def getEndpoints(self, device_id):
        for e in self.endpoint_list:
            if e.token is not None and e.token == device_id and e.enabled == True:
                return e
        return None

    def send(self, endpoint, msg, msg_structure):
        publish_result = self.sns.publish(target_arn=endpoint.arn, message=msg, message_structure=msg_structure)
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
