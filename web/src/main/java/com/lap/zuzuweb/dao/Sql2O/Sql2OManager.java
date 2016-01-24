package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;

public class Sql2OManager {

	static private Sql2o instance = null;
	
	static public Sql2o getSql2o(){
		if (instance == null){
			instance = createSql2O();
		}
		return instance;
	}
	
	static private Sql2o createSql2O(){
		String dbHost ="ec2-52-77-238-225.ap-southeast-1.compute.amazonaws.com";
		String dbPort = "5432";
		String database = "zuzu_rentals_db";
		String dbUsername = "zuzu";
		String dbPassword = "ji3g4xu3ej;jo3";
				
        Sql2o sql2o = new Sql2o("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + database,
                dbUsername, dbPassword);
        
        return sql2o;
	}
	
	
}
