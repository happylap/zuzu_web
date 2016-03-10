package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;
import org.sql2o.quirks.Quirks;
import org.sql2o.quirks.QuirksDetector;

import com.lap.zuzuweb.Secrets;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Sql2OManager {

	static private Sql2o instance = null;

	static public Sql2o getSql2o() {
		if (instance == null) {
			instance = createSql2O();
		}
		return instance;
	}

	static private Sql2o createSql2O() {
		String url = "jdbc:postgresql://" + Secrets.DB_HOST + ":" + Secrets.DB_PORT + "/" + Secrets.DB_NAME;

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl(url);
		cpds.setUser(Secrets.DB_USERNAME);
		cpds.setPassword(Secrets.DB_PASSWORD);
		// the settings below are optional -- c3p0 can work with defaults
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);

		Quirks quirks = QuirksDetector.forURL(url);

		return new Sql2o(cpds, quirks);
	}

}
