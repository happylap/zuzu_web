# encoding: utf-8

import sys
import logging
import boto3
import aiobotocore

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1

if IS_LOCAL:
    from zuzuNotify import LocalConstant
else:
    import LocalConstant

class SNS_Enpoint(object):
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

class SNSClient(object):

    def __init__(self):
        self.client = boto3.client(
            'sns',
            aws_access_key_id=LocalConstant.AWS_ACCESS_KEY_ID,
            aws_secret_access_key=LocalConstant.AWS_SECRET_ACCESS_KEY,
            region_name = LocalConstant.AWS_REGION
        )
        self.logger = logging.getLogger(__name__)

    def getEnpoints(self):
        endpoint_list = []
        try:
            paginator = self.client.get_paginator('list_endpoints_by_platform_application')
            page_iterator = paginator.paginate(PlatformApplicationArn=LocalConstant.SNS_PLATFORM)
            for page in page_iterator:
                endpoints = page.get('Endpoints')
                if endpoints is not None or len(endpoints) >0:
                    for e in endpoints:
                        arn = e.get('EndpointArn')
                        enable = e.get('Attributes').get('Enabled')
                        token = e.get('Attributes').get('Token')
                        endpoint_list.append(SNS_Enpoint(arn,enable,token))
        except:
            self.logger.error("Fail to getEnpoints")
        return endpoint_list

    def getNextEnpoints(self, next_token):
        endpoint_list = []
        try:
            response = self.client.list_endpoints_by_platform_application(PlatformApplicationArn=LocalConstant.SNS_PLATFORM)
            endpoints = response.get('Endpoints')
            if endpoints is None or len(endpoints) <=0:
                return endpoint_list
            for e in endpoints:
                arn = e.get('EndpointArn')
                enable = e.get('Attributes').get('Enabled')
                token = e.get('Attributes').get('Token')
                endpoint_list.append(SNS_Enpoint(arn,enable,token))
        except:
            pass
        return endpoint_list

class AsyncSNSClient(object):

    def __init__(self, loop):
        session = aiobotocore.get_session(loop=loop)

        self.async_client = session.create_client('sns', region_name=LocalConstant.AWS_REGION,
                            aws_secret_access_key=LocalConstant.AWS_SECRET_ACCESS_KEY,
                            aws_access_key_id=LocalConstant.AWS_ACCESS_KEY_ID)

        self.logger = logging.getLogger(__name__)

    async def send(self, endpoint, msg, msg_structure):
        try:
            response = await self.async_client.publish(TargetArn=endpoint.arn, Message=msg, MessageStructure=msg_structure)
            self.logger.info(response)
            if response.get('ResponseMetadata').get('HTTPStatusCode') == 200:
                self.logger.info("Notify successfully, endpoint: "  + str(endpoint.arn))
            else:
                self.logger.error("Notify failed, endpoint: "  + str(endpoint.arn))
        except:
            self.logger.error("Notify exception, endpoint: "  + str(endpoint.arn))

    def close(self):
        self.async_client.close()
