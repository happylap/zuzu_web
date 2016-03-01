package com.lap.zuzuweb.model;

import java.io.InputStream;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class User
{
	public enum UserProvider {
		FB, GOOGLE
	}

    private String user_id;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private Date register_time;
    
    private UserProvider provider;
    private String email;
    private String name;
    private String gender;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    private Date birthday;
    private String picture_url;
    private InputStream purchase_receipt;
    
}