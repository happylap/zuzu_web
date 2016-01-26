package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class User
{
    private String user_id;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="Asia/Taipei")
    private Date register_time;
    private String facebook_id;
    private String facebook_name;
    private String facebook_email;
    private String facebook_picture_url;
    private String facebook_first_name;
    private String facebook_last_name;
    private String facebook_gender;
    private Date facebook_birthday;
}