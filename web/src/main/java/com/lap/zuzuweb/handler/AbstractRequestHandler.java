package com.lap.zuzuweb.handler;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.Validable;

import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestHandler<V extends Validable> implements RequestHandler<V>, Route {

    private Class<V> valueClass;

    private static final int HTTP_BAD_REQUEST = 400;

    public AbstractRequestHandler(Class<V> valueClass){
        this.valueClass = valueClass;
    }

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
    
    /*private static boolean shouldReturnHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }*/


    /*public final Answer process(V value, Map<String, String> urlParams, boolean shouldReturnHtml) {
        if (value != null && !value.isValid()) {
            return new Answer(HTTP_BAD_REQUEST);
        } else {
            return processImpl(value, urlParams, shouldReturnHtml);
        }
    }

    protected abstract Answer processImpl(V value, Map<String, String> urlParams, boolean shouldReturnHtml);*/

    public final Answer process(V value, Map<String, String> urlParams) {
	    if (value != null && !value.isValid()) {
	        return new Answer(HTTP_BAD_REQUEST);
	    } else {
	        return processImpl(value, urlParams);
	    }
	}

    protected abstract Answer processImpl(V value, Map<String, String> urlParams);

    @Override
    public Object handle(Request request, Response response) throws Exception {
    	
    	if (!this.auth(request.headers("Authorization"))) {
    		response.status(403);
            response.body("Forbidden");
            return new Answer(403, "Forbidden");
    	}
    	
        try {
           	ObjectMapper objectMapper = new ObjectMapper();
            V value = null;
            if (valueClass != EmptyPayload.class) {
                value = objectMapper.readValue(request.body(), valueClass);
            }
            
            Map<String, String> urlParams = request.params();
            //Answer answer = process(value, urlParams, shouldReturnHtml(request));
            /*if (shouldReturnHtml(request)) {
                response.type("text/html");
            } else {
                response.type("application/json");
            }*/
            Answer answer = process(value, urlParams);
            response.status(answer.getCode());
            response.type("application/json");
            response.body(answer.getBody());
            return answer.getBody();
        } catch (Exception e) {
        	e.printStackTrace();
            response.status(400);
            response.body(e.getMessage());
            return e.getMessage();
        }
    }
    
    private boolean auth(String auth_string) {
    	boolean auth = false;
    	
    	final Base64.Encoder encoder = Base64.getEncoder();
    	String encodedAuthToken = encoder.encodeToString(Secrets.AUTH_TOKEN.getBytes());
    	
    	if (("Basic " + encodedAuthToken).equals(auth_string)) {
    		auth = true; 
    	}
    	return auth;
    }
}
