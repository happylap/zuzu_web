package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ServicePayload {

	private String user_id;
	private String status; // valid, invalid
	private Long remaining_second;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date expire_time;
	private int valid_purchase_count;
	private int invalid_purchase_count;
	
}
