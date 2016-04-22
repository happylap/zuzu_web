package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Log {
	
	public enum Type {
		REGISTER_TIME,
		RECEIVE_NOTIFY_TIME,
		EXPIRE_TIME;
	}
	
    private String device_id;
    private String user_id;
    private String log_type;
    private String log_comment;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private Date log_time;
}
