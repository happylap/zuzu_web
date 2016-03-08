package com.lap.zuzuweb.handler.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.lap.zuzuweb.model.Criteria;

import lombok.Data;

@Data
public class CriteriaCreatePayload implements Validable {
	
    private boolean enabled ;
    private JsonNode filters;
    
    @Override
	public boolean isValid() {
		return true;
	}
    
	public Criteria toCriteria(String user_id)
	{
		Criteria c = new Criteria();
		c.setUser_id(user_id);
		c.setEnabled(this.enabled);
		if (this.filters != null) {
			c.setFiltersValue(this.filters.toString());
		}
		return c;
	}
}
