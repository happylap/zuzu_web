# encoding: utf-8

import datetime
import sys

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1


if IS_LOCAL:
    from zuzuNotify import LocalConstant, TimeUtils
    from zuzuNotify.zuzu_solr import SolrClient
else:
    import LocalConstant, TimeUtils
    from zuzu_solr import SolrClient


def main():
    post_time = datetime.datetime.utcnow() - datetime.timedelta(days=7)
    notify_solr = SolrClient(LocalConstant.NOTIFIER_SOLR_URL)
    notify_solr.deleteOldItems(TimeUtils.getTimeString(post_time, TimeUtils.UTC_FORMT))


if __name__=="__main__":
    main()