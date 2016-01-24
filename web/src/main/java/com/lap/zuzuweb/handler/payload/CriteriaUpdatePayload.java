package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.Criteria;

public class CriteriaUpdatePayload extends Criteria implements Validable {

	@Override
	public boolean isValid() {
		return this.getCriteria_id() != null && this.getUser_id() != null;
	}

}