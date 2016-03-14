package com.lap.zuzuweb.handler.payload;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class NotifierPayload {

	private String user_id;
	private List<String> device_id;
	private String criteria_id;
	private String filters;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date last_notify_time;
	
}
