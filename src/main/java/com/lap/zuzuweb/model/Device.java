package com.lap.zuzuweb.model;

import java.util.Date;

import lombok.Data;

@Data
public class Device {
    private String device_id;
    private String user_id;
    private boolean enabled;
    private Date register_time;
    private Date last_notify_time;
}
