# encoding: utf-8

import sys

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1

if IS_LOCAL:
    from notify import secrets
else:
    import secrets

AWS_ACCESS_KEY_ID = secrets.AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY = secrets.AWS_SECRET_ACCESS_KEY
AWS_REGION = secrets.AWS_REGION


# WEB TOKEN
WEB_TOKEN_HEADER = secrets.WEB_TOKEN_HEADER
WEB_TOKEN_VALUE = secrets.WEB_TOKEN_VALUE

#ZUZU_EMAIl
ZUZU_EMAIL_USER = secrets.ZUZU_EMAIL_USER
ZUZU_EMAIL_PASSWD = secrets.ZUZU_EMAIL_PASSWD
ZUZU_EMAIL_ADMIN = secrets.ZUZU_EMAIL_ADMIN
ZUZU_EMAIL_CC = secrets.ZUZU_EMAIL_CC


# Log folder
LOG_FOLDER = secrets.LOG_FOLDER


# Poduction mode / test mode
TEST_PERFORMANCE = secrets.TEST_PERFORMANCE
PRODUCT_MODE = secrets.PRODUCT_MODE

# SNS
APNS_MSG_HEADR = secrets.APNS_MSG_HEADR
SNS_PLATFORM = secrets.SNS_PLATFORM

#Solr
ZUZU_SOLR_URL = secrets.ZUZU_SOLR_URL
NOTIFIER_SOLR_URL = secrets.NOTIFIER_SOLR_URL

# ZUZU Web
WEB_URL = secrets.WEB_URL

# complement
complement_mode = "complement"