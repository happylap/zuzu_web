package com.lap.zuzuweb.model;

import java.util.Date;

import lombok.Data;

@Data
public class NotifyItem {
    private String item_id;
    private String user_id;
    private String criteria_id;
    private Date notify_time;
    private boolean is_read;
    private int price;
    private double size;
    private String first_img_url;
    private int house_type;
    private int purpose_type;
    private String title;
    private String addr;
}
