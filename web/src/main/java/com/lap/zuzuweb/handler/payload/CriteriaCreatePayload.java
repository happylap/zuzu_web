package com.lap.zuzuweb.handler.payload;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.lap.zuzuweb.model.Criteria;

import lombok.Data;

@Data
public class CriteriaCreatePayload implements Validable {

    private String user_id ;
    private boolean enabled ;
    private Date expire_time;
    private String apple_product_id;
    private JsonNode query_string;
    
	@Override
	public boolean isValid() {
		return this.getUser_id() != null;
	}

	public Criteria toCriteria()
	{
		Criteria c = new Criteria();
		c.setUser_id(this.user_id);
		c.setEnabled(this.enabled);
		c.setExpire_time(this.expire_time);
		c.setApple_product_id(this.apple_product_id);
		c.setQuery_string(this.query_string.toString());
		return c;
	}
}
