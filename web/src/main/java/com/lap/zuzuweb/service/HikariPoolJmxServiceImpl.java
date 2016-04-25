package com.lap.zuzuweb.service;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class HikariPoolJmxServiceImpl implements HikariPoolJmxService {

	private final ObjectName poolAccessor;
	private final ObjectName poolConfigAccessor;
	private final MBeanServer mBeanServer;
	private final String poolName;

	public HikariPoolJmxServiceImpl(final String poolName) {
		this.poolName = poolName;
		try {
			mBeanServer = ManagementFactory.getPlatformMBeanServer();
			poolConfigAccessor = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + poolName + ")");
			poolAccessor = new ObjectName("com.zaxxer.hikari:type=Pool (" + poolName + ")");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException("Pool " + poolName + " could not be found", e);
		}
	}

	public String getPoolName() {
		return poolName;
	}

	@Override
	public int getIdleConnections() {
		return getCount("IdleConnections");
	}

	@Override
	public int getActiveConnections() {
		return getCount("ActiveConnections");
	}

	@Override
	public int getTotalConnections() {
		return getCount("TotalConnections");
	}

	@Override
	public int getThreadsAwaitingConnection() {
		return getCount("ThreadsAwaitingConnection");
	}

	@Override
	public void softEvictConnections() {
		invokeMethod("softEvictConnections");
	}

	@Override
	public void suspendPool() {
		invokeMethod("suspendPool");
	}

	@Override
	public void resumePool() {
		invokeMethod("resumePool");
	}

	@Override
	public long getConnectionTimeout() {
		return getConfigNumber("ConnectionTimeout").longValue();
	}

	@Override
	public long getIdleTimeout() {
		return getConfigNumber("IdleTimeout").longValue();
	}

	@Override
	public long getLeakDetectionThreshold() {
		return getConfigNumber("LeakDetectionThreshold").longValue();
	}

	@Override
	public long getMaxLifetime() {
		return getConfigNumber("MaxLifetime").longValue();
	}

	@Override
	public int getMinimumIdle() {
		return getConfigNumber("MinimumIdle").intValue();
	}

	@Override
	public int getMaximumPoolSize() {
		return getConfigNumber("MaximumPoolSize").intValue();
	}

	@Override
	public long getValidationTimeout() {
		return getConfigNumber("ValidationTimeout").longValue();
	}

	protected int getCount(String attributeName) {

		try {
			return (Integer) mBeanServer.getAttribute(poolAccessor, attributeName);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void invokeMethod(String methodName) {

		try {
			mBeanServer.invoke(poolAccessor, methodName, null, null);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Number getConfigNumber(String attributeName) {

		try {
			return (Number) mBeanServer.getAttribute(poolConfigAccessor, attributeName);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
