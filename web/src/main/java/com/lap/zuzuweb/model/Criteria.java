package com.lap.zuzuweb.model;

import java.util.Date;

import lombok.Data;

@Data
public class Criteria {

    private String criteria_id;
    private String user_id ;
    private boolean enabled ;
    private Date expire_time;
    private String apple_product_id;
    private String query_string;
}
