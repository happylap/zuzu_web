package com.lap.zuzuweb.service;

public interface HikariPoolJmxService {

	public String getPoolName();

	public int getIdleConnections();

	public int getActiveConnections();

	public int getTotalConnections();

	public int getThreadsAwaitingConnection();

	public void softEvictConnections();

	public void suspendPool();

	public void resumePool();

	public long getConnectionTimeout();

	public long getIdleTimeout();

	public long getLeakDetectionThreshold();

	public long getMaxLifetime();

	public int getMinimumIdle();

	public int getMaximumPoolSize();

	public long getValidationTimeout();

}
