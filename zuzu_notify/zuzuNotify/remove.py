# encoding: utf-8

import datetime
import sys, os
import glob
import logging

args = sys.argv
IS_LOCAL = 0
if len(args) > 1 and args[1] == "IS_LOCAL":
    IS_LOCAL = 1


if IS_LOCAL:
    from zuzuNotify import LocalConstant, TimeUtils
else:
    import LocalConstant, TimeUtils


def main():
    now_str = TimeUtils.getTimeString(TimeUtils.get_Now(), TimeUtils.UTC_FORMT)
    logname = LocalConstant.LOG_FOLDER+"remove"+"_%s.log" % now_str
    logging.basicConfig(filename=logname,
                filemode='wb',
                format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                datefmt='%H:%M:%S',
                level=logging.INFO)

    logger = logging.getLogger("remove")

    logger.info("remove old log files")
    file_list = []
    compare = TimeUtils.get_Now() - datetime.timedelta(days=7) #7 days ago in UTC time
    logger.info("compare:" + str(compare))
    log_files = glob.glob(LocalConstant.LOG_FOLDER+"*.log")
    file_list.extend(log_files)

    if len(file_list) <=0:
        logger.info("no files to be removed")
        sys.exit()

    try:
        for file_name in file_list:
            logger.info("remove file_name:" + str(file_name))
            if os.path.isfile(file_name) and os.access(file_name, os.R_OK):
                last_modified_date = datetime.datetime.fromtimestamp(os.path.getmtime(file_name))
                if last_modified_date < compare:
                    logger.info( "remove file: " +file_name )
                    os.remove(file_name)

        logger.info("remove old log files done!")
    except:
        logger.error("error when cleaning out-of date files")

if __name__=="__main__":
    main()
