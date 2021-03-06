package com.lap.zuzuweb.handler;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.payload.Validable;
import com.lap.zuzuweb.util.CommonUtils;

import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestArrayHandler implements RequestArrayHandler, Route {
 
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(AbstractRequestArrayHandler.class);
	
    public final Answer process(Validable[] values, Map<String, String> urlParams) {
    	for (Validable value: values){
    		if (value != null && !value.isValid()) {
    			return Answer.bad_request();
    		}
    	}
    	
    	return processImpl(values, urlParams);
	}

    protected abstract Answer processImpl(Validable[] values, Map<String, String> urlParams);

    protected abstract Validable[] parsePayloas(String reqBody) throws JsonMappingException, IOException;
    
    @Override
    public Object handle(Request request, Response response) throws Exception {
    	Answer answer = null;
    	
        try {
        	Validable[] values = parsePayloas(request.body());
            Map<String, String> urlParams = request.params();
            answer = process(values, urlParams);            
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
