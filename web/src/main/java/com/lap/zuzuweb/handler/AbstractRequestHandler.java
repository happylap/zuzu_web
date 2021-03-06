package com.lap.zuzuweb.handler;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestHandler<V extends Validable> implements RequestHandler<V>, Route {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(AbstractRequestHandler.class);
	
	private Class<V> valueClass;

	public AbstractRequestHandler(Class<V> valueClass) {
		this.valueClass = valueClass;
	}

	public final Answer process(V value, Map<String, String> urlParams) {
		if (value != null && !value.isValid()) {
			return Answer.bad_request();
		} else {
			return processImpl(value, urlParams);
		}
	}

	protected abstract Answer processImpl(V value, Map<String, String> urlParams);

	@Override
	public Object handle(Request request, Response response) throws Exception {
		Answer answer = null;
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			V value = null;
			if (valueClass != EmptyPayload.class) {
				value = objectMapper.readValue(request.body(), valueClass);
			}
			Map<String, String> urlParams = request.params();
			answer = process(value, urlParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
            answer = Answer.error(e.getMessage());
		}
		
		response.status(200);
        response.type("application/json");
        response.body(CommonUtils.toJson(answer));
        
		return response.body();
	}

}
