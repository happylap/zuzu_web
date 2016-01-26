# encoding: utf-8

import logging, re
from solr import Solr, SolrException

class NotifierSolr(object):
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

    def getNotifyItems(self, post_time, filters):
        query = "post_time:["+post_time+" TO *]"
        filters = filters
        columns = ["*"]
        sort = ["post_time asc"]
        rows = self._get_rows(query, filters)
        if rows:
            r = self.solr.query(query=query, filters=filters, columns= columns, rows=rows, sort=sort)
            docs = r.documents
            return docs
        else:
            return None

    def add(self):
        self.solr.add()

    def commit(self):
        self.solr.commit()

