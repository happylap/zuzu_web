package com.lap.zuzuweb.dao.Sql2O;

import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

import com.lap.zuzuweb.App;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.ZuzuLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Sql2OManager {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(Sql2OManager.class);
	
	static private Sql2o instance = null;

	static public Sql2o getSql2o() {
		if (instance == null) {
			instance = createSql2O();
		}
		return instance;
	}
	
	static private Sql2o createSql2O() {
		logger.entering("createSql2O");
		
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
			
			logger.exit("createSql2O", "Create a Sql2O instance successful.");
			return sql2o;
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
