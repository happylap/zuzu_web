package com.lap.zuzuweb.handler.payload;

import lombok.Data;

@Data
public class PatchPayload implements Validable
{
	final public static String OP_REPLACE = "replace";
	
	private String op;
	private String path;
	private String value;
	@Override
	public boolean isValid() {
		return this.getOp() != null && this.getPath() != null && this.getValue() != null;
	}
}
