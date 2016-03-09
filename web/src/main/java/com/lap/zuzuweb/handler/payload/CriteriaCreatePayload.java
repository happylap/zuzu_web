package com.lap.zuzuweb.handler.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.lap.zuzuweb.model.Criteria;

import lombok.Data;

@Data
public class CriteriaCreatePayload implements Validable {
	
	private String user_id;
    private boolean enabled ;
    private JsonNode filters;
    
    @Override
	public boolean isValid() {
		return true;
	}
    
	public Criteria toCriteria()
	{
		Criteria c = new Criteria();
		c.setUser_id(this.user_id);
		c.setEnabled(this.enabled);
		if (this.filters != null) {
			c.setFiltersValue(this.filters.toString());
		}
		return c;
	}
}
