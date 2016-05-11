# encoding: utf-8

import datetime
import sys

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1


if IS_LOCAL:
    from notify import constants, timeutils
    from notify.zuzu_solr import SolrClient
else:
    import LocalConstant, TimeUtils
    from zuzu_solr import SolrClient


def main():
    post_time = datetime.datetime.utcnow() - datetime.timedelta(days=7)
    notify_solr = SolrClient(constants.NOTIFIER_SOLR_URL)
    notify_solr.delete_old_items(timeutils.get_time_str(post_time, timeutils.UTC_FORMT))


if __name__=="__main__":
    main()