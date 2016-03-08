package com.lap.zuzuweb.model;

import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.lap.zuzuweb.handler.payload.Validable;

import lombok.Data;

@Data
public class User implements Validable {
	
	private String user_id;
	private String email;
	private Date register_time;
	private String name;
	private String gender;
	private Date birthday;
	private String picture_url;
	private InputStream purchase_receipt;
	private Date update_time;

	@Override
	public boolean isValid() {
		return StringUtils.isNotBlank(user_id) && StringUtils.isNotBlank(email) && this.register_time != null;
	}

}