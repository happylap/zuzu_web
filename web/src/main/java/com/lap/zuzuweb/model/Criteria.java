package com.lap.zuzuweb.model;

import java.util.Date;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Criteria 
{
	public Criteria()
	{
		this.filters = new PGobject();
		this.filters.setType("json");
	}
	
	public void setFiltersValue(String str)
	{
		try
		{
			this.filters.setValue(str);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
    private String criteria_id;
    private String user_id ;
    private boolean enabled ;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date expire_time;
    private String apple_product_id;
    private PGobject filters;
    
}
