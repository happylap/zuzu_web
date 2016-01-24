package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.Device;

public class DeviceUpdatePayload extends Device implements Validable
{
    @Override
	public boolean isValid() {
    	return this.getDevice_id() != null && this.getUser_id() != null;
	}
}
