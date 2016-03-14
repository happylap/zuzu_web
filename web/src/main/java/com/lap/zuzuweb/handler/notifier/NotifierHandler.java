package com.lap.zuzuweb.handler.notifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.NotifierPayload;
import com.lap.zuzuweb.model.Criteria;
import com.lap.zuzuweb.model.Device;
import com.lap.zuzuweb.service.CriteriaService;
import com.lap.zuzuweb.service.DeviceService;

public class NotifierHandler extends AbstractRequestHandler<EmptyPayload>{

	private CriteriaService criteriaSvc = null;
	private DeviceService deviceSvc = null;
	
	public NotifierHandler(CriteriaService criteriaSvc, DeviceService deviceSvc) {
        super(EmptyPayload.class);
        this.criteriaSvc = criteriaSvc;
        this.deviceSvc = deviceSvc;
    }
	
	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		
		List<Criteria> criterias = this.criteriaSvc.getValidCriteria();
		
		if (CollectionUtils.isNotEmpty(criterias)) {
			
			List<Device> devices = this.deviceSvc.getValidDevices();
			
			Map<String, List<String>> deviceIdsGroup = new HashMap<String, List<String>>();
			
			if (CollectionUtils.isNotEmpty(devices)) {
				for (Device d: devices) {
					String key = d.getUser_id();
					if (!deviceIdsGroup.containsKey(key)) {
						deviceIdsGroup.put(key, new ArrayList<String>());
					}
					deviceIdsGroup.get(key).add(d.getDevice_id());
				}
			}
			
			List<NotifierPayload> payloads = new ArrayList<NotifierPayload>();
			for (Criteria c: criterias) {
				String userId = c.getUser_id();
				
				NotifierPayload payload = new NotifierPayload();
				payload.setUser_id(userId);
				payload.setCriteria_id(c.getCriteria_id());
				payload.setFilters(c.getFilters().getValue());
				payload.setLast_notify_time(c.getLast_notify_time());
				if (deviceIdsGroup.containsKey(userId)) {
					payload.setDevice_id(deviceIdsGroup.get(userId));
				} else {
					payload.setDevice_id(new ArrayList<String>());
				}
				payloads.add(payload);
			}
			
			return Answer.ok(payloads);
		}
		
        return Answer.no_data();
	}

}