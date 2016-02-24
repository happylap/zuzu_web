package com.lap.zuzuweb.handler;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestHandler<V extends Validable> implements RequestHandler<V>, Route {

	private Class<V> valueClass;

	public AbstractRequestHandler(Class<V> valueClass) {
		this.valueClass = valueClass;
	}

	public final Answer process(V value, Map<String, String> urlParams) {
		if (value != null && !value.isValid()) {
			throw new RuntimeException("HTTP BAD REQUEST");
		} else {
			return processImpl(value, urlParams);
		}
	}

	protected abstract Answer processImpl(V value, Map<String, String> urlParams);

	@Override
	public Object handle(Request request, Response response) throws Exception {
		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			V value = null;
			if (valueClass != EmptyPayload.class) {
				value = objectMapper.readValue(request.body(), valueClass);
			}
			Map<String, String> urlParams = request.params();

			Answer answer = process(value, urlParams);

            response.status(200);
            response.type("application/json");
			return CommonUtils.toJson(answer);
			
		} catch (Exception e) {
			
			e.printStackTrace();
            response.status(200);
            response.type("application/json");
			return CommonUtils.toJson(Answer.error(e.getMessage()));
			
		}
	}

}
