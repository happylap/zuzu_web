package com.lap.zuzuweb.dao.Sql2O;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

import com.lap.zuzuweb.App;
import com.lap.zuzuweb.Secrets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Sql2OManager {

	private static final Logger logger = LoggerFactory.getLogger(Sql2OManager.class);
	
	static private Sql2o instance = null;

	static public Sql2o getSql2o() {
		if (instance == null) {
			instance = createSql2O();
		}
		return instance;
	}
	
	static private Sql2o createSql2O() {
		logger.info("Create a Sql2O instance...");
		try {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:postgresql://" + Secrets.DB_HOST + ":" + Secrets.DB_PORT + "/" + Secrets.DB_NAME);
			config.setUsername(Secrets.DB_USERNAME);
			config.setPassword(Secrets.DB_PASSWORD);
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.setMinimumIdle(20);
			config.setMaximumPoolSize(100);
			config.setConnectionTimeout(30000);
			config.setPoolName(App.DB_POOL_NAME);
			config.setRegisterMbeans(true);
			
			HikariDataSource ds = new HikariDataSource(config);
			Sql2o sql2o = new Sql2o(ds, new PostgresQuirks());
			
			logger.info("Create a Sql2O instance successful.");
			return sql2o;
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	static public void close() {
		if (instance != null) {
			HikariDataSource ds = ((HikariDataSource) instance.getDataSource());
			if (ds != null) {
				ds.close();
			}
		}
	}
}
