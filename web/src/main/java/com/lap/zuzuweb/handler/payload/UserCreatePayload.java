package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.User;

public class UserCreatePayload extends User implements Validable
{
    @Override
	public boolean isValid() {
    	return this.getUser_id() != null;
	}
}
