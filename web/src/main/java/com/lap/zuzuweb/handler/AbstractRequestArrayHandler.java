package com.lap.zuzuweb.handler;

import java.io.IOException;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lap.zuzuweb.handler.payload.Validable;

public abstract class AbstractRequestArrayHandler implements RequestArrayHandler, Route {

    private static final int HTTP_BAD_REQUEST = 400;

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
 
    public final Answer process(Validable[] values, Map<String, String> urlParams) {
    	for (Validable value: values){
    		if (value != null && !value.isValid()) {
    			return new Answer(HTTP_BAD_REQUEST);
    		}
    	}
    	
    	return processImpl(values, urlParams);
	}

    protected abstract Answer processImpl(Validable[] values, Map<String, String> urlParams);

    protected abstract Validable[] parsePayloas(String reqBody) throws JsonMappingException, IOException;
    
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
        	Validable[] values = parsePayloas(request.body());
            Map<String, String> urlParams = request.params();
            
            Answer answer = process(values, urlParams);
            response.status(answer.getCode());
            response.type("application/json");
            response.body(answer.getBody());
            return answer.getBody();
        } catch (Exception e) {
            response.status(400);
            response.body(e.getMessage());
            return e.getMessage();
        }
    }

}
