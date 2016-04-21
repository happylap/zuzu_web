# encoding: utf-8

import logging
import boto3
import aiobotocore

from zuzuNotify import LocalConstant

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
                self.logger.info("Notify successfully, endpoint: "  + str(endpoint))
            else:
                self.logger.error("Notify failed, endpoint: "  + str(endpoint))
        except:
            self.logger.error("Notify failed, endpoint: "  + str(endpoint))

    def close(self):
        self.async_client.close()
