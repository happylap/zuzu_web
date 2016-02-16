package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Log {
	
	public enum Type {
		REGISTER_TIME("RegisterTime"),
		RECEIVE_NOTIFY_TIME("ReceiveNotifyTime");
	 
	    private String value;
	 
	    private Type(String value) {
	        this.value = value;
	    }
	 
	    public String getValue() {
	        return this.value;
	    }
	}
	
    private String device_id;
    private String user_id;
    private Type log_type;
    private String log_comment;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="Asia/Taipei")
    private Date log_time;
}
