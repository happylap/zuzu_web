package com.lap.zuzuweb;

import lombok.Data;

@Data
public class ApiResponse {
	private int code;
	private String result;
	private String errorMessage;

	public ApiResponse(String result) {
		this.code = 0;
		this.result = result;
	}

	public ApiResponse(int code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}

}
