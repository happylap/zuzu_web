package com.lap.zuzuweb.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Device {
    private String device_id;
    private String user_id;
    private boolean enabled;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date register_time;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date last_notify_time;
}
