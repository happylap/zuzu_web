package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class NotifyItemErrorMessagePayload {
	private String item_id;
	private String user_id;
	private String message;
}
