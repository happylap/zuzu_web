package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;

import com.lap.zuzuweb.Secrets;

public class Sql2OManager {

	static private Sql2o instance = null;
	
	static public Sql2o getSql2o(){
		if (instance == null){
			instance = createSql2O();
		}
		return instance;
	}
	
	static private Sql2o createSql2O(){
		
		Sql2o sql2o = new Sql2o("jdbc:postgresql://" + Secrets.DB_HOST + ":" + Secrets.DB_PORT + "/" + Secrets.DB_NAME,
        		Secrets.DB_USERNAME, Secrets.DB_PASSWORD);
        
        return sql2o;
	}
	
	
}
