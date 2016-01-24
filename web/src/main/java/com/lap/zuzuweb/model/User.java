package com.lap.zuzuweb.model;

import java.util.Date;

import lombok.Data;

@Data
public class User
{
    private String user_id;
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