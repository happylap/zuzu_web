# encoding: utf-8

import logging, re
from solr import Solr, SolrException

class RHCSolr(object):
    def __init__(self, solr_url):
        self.solr_url = solr_url
        self.solr = Solr(endpoints=self.solr_url, http_cache=False)
        
    def _error_callback(self, document=None, item_id=None, query=None):
        """
        Sends error
        """

    def _get_rows(self, query, filters):
        results = self.solr.query(query=query, filters=filters, rows=0)
        if results is not None:
            return results.results_count
        return 0

    def getNewPostItems(self, post_time):
        query = "*:*"
        filters = {}
        filters["post_time"] = "["+post_time+" TO *]"
        columns = ["*"]
        sort = ["post_time asc"]
        rows = self._get_rows(query, filters)
        if rows:
            r = self.solr.query(query=query, filters=filters, columns= columns, rows=rows, sort=sort)
            docs = r.documents
            return docs
        else:
            return None

    def add(self, item):
        self.solr.add(item)

    def commit(self):
        self.solr.commit()

'''
    def get_last_post_time(self):
        query = "*:*"
        columns = ["id", "post_time"]
        sort = ["post_time desc"]
        rows = 1
        r = self.solr.query(query=query, columns= columns, rows=rows, sort=sort)
        docs = r.documents
        if len(docs) > 0:
            item = docs[0]
            print item["id"]
            print item["post_time"]
'''