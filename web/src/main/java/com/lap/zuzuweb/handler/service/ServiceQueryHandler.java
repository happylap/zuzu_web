package com.lap.zuzuweb.handler.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.EmptyPayload;
import com.lap.zuzuweb.handler.payload.ServicePayload;
import com.lap.zuzuweb.model.Purchase;
import com.lap.zuzuweb.model.Service;
import com.lap.zuzuweb.service.PurchaseService;
import com.lap.zuzuweb.service.UserService;
import com.lap.zuzuweb.util.CommonUtils;

public class ServiceQueryHandler extends AbstractRequestHandler<EmptyPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(ServiceQueryHandler.class);

	private UserService userSvc = null;

	private PurchaseService purchaseSvc = null;

	public ServiceQueryHandler(UserService userSvc, PurchaseService purchaseSvc) {
		super(EmptyPayload.class);
		this.userSvc = userSvc;
		this.purchaseSvc = purchaseSvc;
	}

	@Override
	protected Answer processImpl(EmptyPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);

		if (!urlParams.containsKey(":userid")) {
			return Answer.bad_request();
		}

		String userId = urlParams.get(":userid");

		purchaseSvc.processService(userId);

		Optional<Service> existService = this.userSvc.getService(userId);

		String status = "invalid";
		Date expire_time = null;
		Date start_time = null;
		Long remaining_second = null;
		Long total_second = null;

		if (existService.isPresent()) {
			Service service = existService.get();

			expire_time = service.getExpire_time();
			start_time = service.getStart_time();

			if (expire_time != null) {
				Date now = CommonUtils.getUTCNow();
				if (expire_time.getTime() > now.getTime()) {
					remaining_second = ((expire_time.getTime() - now.getTime()) / 1000);
				} else {
					remaining_second = Long.valueOf(0);
				}

				if (start_time != null) {
					if (expire_time.getTime() > start_time.getTime()) {
						total_second = ((expire_time.getTime() - start_time.getTime()) / 1000);
					} else {
						total_second = Long.valueOf(0);
					}
				}
			}

			if (remaining_second > 0) {
				status = "valid";
			}
		}

		int valid_purchase_count = 0;
		int invalid_purchase_count = 0;

		List<Purchase> purchases = this.purchaseSvc.getPurchase(userId);

		if (CollectionUtils.isNotEmpty(purchases)) {
			valid_purchase_count = (int) purchases.stream().filter(p -> p.is_valid()).count();
			invalid_purchase_count = (int) purchases.stream().filter(p -> !p.is_valid()).count();
		}

		ServicePayload payload = new ServicePayload();
		payload.setUser_id(userId);
		payload.setStatus(status);
		payload.setTotal_second(total_second);
		payload.setRemaining_second(remaining_second);
		payload.setExpire_time(expire_time);
		payload.setStart_time(start_time);
		payload.setValid_purchase_count(valid_purchase_count);
		payload.setInvalid_purchase_count(invalid_purchase_count);

		return Answer.ok(payload);
	}
}
