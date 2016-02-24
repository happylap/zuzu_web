package com.lap.zuzuweb.handler;

public class Answer {
	
	private static int OK = 200;
	private static int NO_DATA = 204;
	private static int BAD_REQUEST = 400;
	private static int FORBIDDEN = 403;
	private static int INTERNAL_SERVER_ERROR = 500;

	public static Answer ok() {
        return new Answer(OK);
    }
	
	public static Answer ok(Object result) {
        return new Answer(OK, result);
    }
	
	public static Answer no_data() {
        return new Answer(NO_DATA);
    }

    public static Answer error(String message) {
        return new Answer(INTERNAL_SERVER_ERROR, null, message);
    }
    
	public static Answer bad_request() {
        return new Answer(BAD_REQUEST, null, "Server 400 bad request");
    }
	
	public static Answer forbidden() {
        return new Answer(FORBIDDEN, null, "Server 403 forbidden");
    }
	
	private int code;
    private Object data;
    private String message;
    
    private Answer(int code) {
        this.code = code;
    }
    
    private Answer(int code, Object data){
        this.code = code;
        this.data = data;
    }
    
    private Answer(int code, Object data, String message){
        this.code = code;
        this.data = data;
        this.message = message;
    }
    
	public int getCode() {
		return code;
	}

	public Object getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Answer [code=" + code + ", data=" + data + ", message=" + message + "]";
	}

}
