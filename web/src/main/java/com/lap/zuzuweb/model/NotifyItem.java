package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class NotifyItem {
    private String item_id;
    private String user_id;
    private String criteria_id;
    private boolean is_read;
    private int price;
    private double size;
    private String first_img_url;
    private int house_type;
    private int purpose_type;
    private String title;
    private String addr;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="Asia/Taipei")
    private Date notify_time;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="Asia/Taipei")
    private Date post_time;
}
