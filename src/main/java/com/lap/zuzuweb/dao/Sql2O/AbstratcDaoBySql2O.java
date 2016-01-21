package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;

public abstract class AbstratcDaoBySql2O
{
	protected Sql2o sql2o = null;
	
    public AbstratcDaoBySql2O() 
    {
        this.sql2o = Sql2OManager.getSql2o();
    }
}
