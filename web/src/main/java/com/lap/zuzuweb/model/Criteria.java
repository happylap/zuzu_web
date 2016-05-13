package com.lap.zuzuweb.model;

import java.util.Date;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lap.zuzuweb.ZuzuLogger;

import lombok.Data;

@Data
public class Criteria {
	private static final ZuzuLogger logger = ZuzuLogger.getLogger(Criteria.class);
	
	public Criteria() {
		this.filters = new PGobject();
		this.filters.setType("json");
	}

	public void setFiltersValue(String str) {
		try {
			this.filters.setValue(str);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String criteria_id;
	private String user_id;
	private boolean enabled;
	private PGobject filters;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date last_notify_time;
}
