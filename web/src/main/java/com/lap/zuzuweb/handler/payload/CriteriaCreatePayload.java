package com.lap.zuzuweb.handler.payload;

import com.lap.zuzuweb.model.Criteria;

public class CriteriaCreatePayload extends Criteria implements Validable {

	@Override
	public boolean isValid() {
		return this.getUser_id() != null && this.getCriteria_id() == null;
	}

}
