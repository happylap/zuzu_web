package com.lap.zuzuweb.handler.criteria;

import java.util.Map;
import java.util.Optional;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.util.CommonUtils;

public class CriteriaValidQueryHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService criteriaSvc = null;
	private UserService userSvc = null;
	
	public CriteriaValidQueryHandler(CriteriaService criteriaSvc, UserService userSvc) {
        super(EmptyPayload.class);
        this.criteriaSvc = criteriaSvc;
        this.userSvc = userSvc;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		
		if (!urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}
		
		String userId = urlParams.get(":userid");
		    
        Optional<Criteria> existCriteria = this.criteriaSvc.getSingleCriteria(userId);
        Optional<Service> existService = this.userSvc.getService(userId);
        
        if (existCriteria.isPresent() && existService.isPresent()) {
        	Service service = existService.get();
        	if (service.getExpire_time() != null && service.getExpire_time().after(CommonUtils.getUTCNow())) {
        		return Answer.ok(existCriteria.get());
        	}
        }
        
        return Answer.no_data();
	}

}