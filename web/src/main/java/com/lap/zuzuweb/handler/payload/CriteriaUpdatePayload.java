package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.Criteria;

import lombok.Data;

@Data
public class CriteriaUpdatePayload extends CriteriaCreatePayload implements Validable
{

	private String criteria_id; 
	
	
	@Override
	public boolean isValid() {
		return this.getCriteria_id() != null && this.getUser_id() != null;
	}

	@Override
	public Criteria toCriteria()
	{
		Criteria c = super.toCriteria();
		c.setCriteria_id(this.criteria_id);
		return c;
	}
}
