package com.lap.zuzuweb.handler.system;

import java.util.LinkedHashMap;
import java.util.Map;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.service.HikariPoolJmxService;

public class DBInfoQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private HikariPoolJmxService service = null;

	public DBInfoQueryHandler(HikariPoolJmxService service) {
		super(EmptyPayload.class);
		this.service = service;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		try {
			Map<String, String> info = new LinkedHashMap<String, String>();
			info.put("activeConnections", "" + service.getActiveConnections());
			info.put("idleConnections", "" + service.getIdleConnections());
			info.put("totalConnections", "" + service.getTotalConnections());
			info.put("threadsAwaitingConnection", "" + service.getThreadsAwaitingConnection());
			info.put("connectionTimeout", "" + service.getConnectionTimeout());
			info.put("idleTimeout", "" + service.getIdleTimeout());
			info.put("leakDetectionThreshold", "" + service.getLeakDetectionThreshold());
			info.put("maxLifetime", "" + service.getMaxLifetime());
			info.put("maxLifetime", "" + service.getMaxLifetime());
			info.put("maximumPoolSize", "" + service.getMaximumPoolSize());
			info.put("validationTimeout", "" + service.getValidationTimeout());
			
			return Answer.ok(info);
		} catch (Exception e) {
			return Answer.error(e.getMessage());
		}

	}

}
