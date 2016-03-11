package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

import com.lap.zuzuweb.Secrets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Sql2OManager {

	static private Sql2o instance = null;

	static public Sql2o getSql2o() {
		if (instance == null) {
			instance = createSql2O();
		}
		return instance;
	}

	static private Sql2o createSql2O() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://" + Secrets.DB_HOST + ":" + Secrets.DB_PORT + "/" + Secrets.DB_NAME);
		config.setUsername(Secrets.DB_USERNAME);
		config.setPassword(Secrets.DB_PASSWORD);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMinimumIdle(20);
		config.setMaximumPoolSize(20);
		config.setConnectionTimeout(30000);
		
		HikariDataSource ds = new HikariDataSource(config);
		return new Sql2o(ds, new PostgresQuirks());
	}

}
