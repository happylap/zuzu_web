package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.lap.zuzuweb.model.Criteria;

import lombok.Data;

@Data
public class CriteriaUpdatePayload implements Validable
{
    private boolean enabled ;
    private JsonNode filters;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date last_notify_time;
	
	@Override
	public boolean isValid() {
		return true;
	}

	public Criteria toCriteria(String criteria_id, String user_id)
	{
		Criteria c = new Criteria();
		c.setCriteria_id(criteria_id);
		c.setUser_id(user_id);
		c.setEnabled(this.enabled);
		if (this.filters != null) {
			c.setFiltersValue(this.filters.toString());
		}
		c.setLast_notify_time(this.last_notify_time);
		return c;
	}
}
