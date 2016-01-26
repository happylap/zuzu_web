# encoding: utf-8

import sys, shutil, time
import glob, logging, json, datetime
from RHC.rhc_solr import RHCSolr
from RHC import JsonUtils, ParseUtils
from RHC import items
from RHC.exporters import UTF8JsonItemExporter
from quicklock import singleton

class PutSolr(object):
    def __init__(self, logger):
        self.logger = logger
        self.rhc_solr = RHCSolr()
        self.rhc_solr._error_callback = self.handle_solr_error
        self.fail_item_list = []
        self.process_items = set()
        
    def handle_solr_error(self, document=None, item_id=None, query=None):
        if document is not None:
            self.logger.error("Add item error:" +document["link"])
            self.fail_item_list.append(document)
        elif item_id is not None:
            self.logger.error("delete item error:" +item_id)
            doc = {}
            doc[items.field_link] = ParseUtils.base64Decode(item_id)
            doc[items.field_id] = item_id
            doc[items.field_is_removed] = 1
            self.fail_item_list.append(doc)
                
    def process(self, jsonFile):
        js = JsonUtils.loadJSON(jsonFile, JsonUtils.UTF8_ENCODE)
        for data in js:
            r = json.dumps(data,ensure_ascii=False)
            doc = json.loads(r)
            doc.pop("_version_", None)
            if doc[items.field_id] in self.process_items:
                continue
            self.process_items.add(doc[items.field_id])
            time.sleep(0.1)
            if items.field_is_removed in doc:
                try:
                    self.rhc_solr.delete_item(doc[items.field_id])
                    self.rhc_solr.commit()
                except SolrException, e:
                    self.logger.error(str(e))
                    self.handle_solr_error(document=item)
                except:
                    self.handle_solr_error(document=item)
            else:
                try:
                    self.rhc_solr.add_item(doc)
                    self.rhc_solr.commit()
                except SolrException, e:
                    self.logger.error(str(e))
                    self.handle_solr_error(document=item)
                except:
                    self.handle_solr_error(document=item)
     
def main():
    try:
        singleton("put_item")
    except:
        print("put_item process is running...")
        sys.exit()

    logname = "log/put_item"+"_%s.log" % datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    logging.basicConfig(filename=logname,
                        filemode='wb',
                        format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                        datefmt='%H:%M:%S',
                        level=logging.INFO)

    logger = logging.getLogger('put_item')
    logger.info("put items start")

    put_solr = PutSolr(logger)
    args = sys.argv
    
    is_update_mode = False
    if len(args) > 1 and "update" == args[1].lower():
        is_update_mode = True
    
    json_files = []
       
    if is_update_mode == True:
        json_files.extend(glob.glob('591/*update_result*.json'))
        json_files.extend(glob.glob('HouseFun/*update_result*.json'))
        json_files.extend(glob.glob('RAK/*update_result*.json'))
        logger.info("there are "+str(len(json_files))+"update json files to put")
    else: 
        json_files.extend(glob.glob('591/*post_result*.json'))
        json_files.extend(glob.glob('HouseFun/*post_result*.json'))
        json_files.extend(glob.glob('RAK/*post_result*.json'))
        logger.info("there are "+str(len(json_files))+"post json files to put")

    total = 0
    for jsonFile in json_files:
        logger.info("process json file: " + jsonFile)
        put_solr.process(jsonFile)
        total = total +1
        shutil.move(jsonFile, "result/")

    fail_item_list = put_solr.fail_item_list
    logger.info(str(len(fail_item_list))+" fail items")
    if len(fail_item_list) > 0:
        f = open("result/put_fail_%s.json" % datetime.datetime.now().strftime("%Y%m%d_%H%M%S"), "wb")
        exporter = UTF8JsonItemExporter(f)
        exporter.start_exporting()
        for item in fail_item_list:
            exporter.export_item(item)
        exporter.finish_exporting()
        f.close()
    
    if is_update_mode == True:
        put_solr.rhc_solr.optimize()
        logger.info("Optimize Solr successfully!!")
    
    logger.info("Totally " + str(total) + " items were put "+ str(len(fail_item_list))+" items failed")
        
main()
    