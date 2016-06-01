package com.lap.zuzuweb.handler.cognito;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.ZuzuLogger;
import com.lap.zuzuweb.handler.AbstractRequestHandler;
import com.lap.zuzuweb.handler.Answer;
import com.lap.zuzuweb.handler.payload.SNSSenderPayload;

public class SNSSenderHandler extends AbstractRequestHandler<SNSSenderPayload> {

	private static final ZuzuLogger logger = ZuzuLogger.getLogger(SNSSenderHandler.class);

	private String ACCESS_KEY = Secrets.AWS_ACCESS_KEY_ID;
	private String SECRET_KEY = Secrets.AWS_SECRET_ACCESS_KEY;
	private String REGION = Secrets.AWS_COGNITO_REGION;

	public SNSSenderHandler() {
		super(SNSSenderPayload.class);
	}

	@Override
	protected Answer processImpl(SNSSenderPayload value, Map<String, String> urlParams) {
		logger.entering("processImpl", "{value: %s, urlParams: %s}", value, urlParams);

		String targetArn = value.getTargetARN();
		
		String message = "This is default message";
		if (StringUtils.isNotBlank(value.getMessage())) {
			message = value.getMessage();
		}

		// Create a client
		AmazonSNSClient service = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
		service.setRegion(RegionUtils.getRegion(REGION));
		PublishRequest publishReq = new PublishRequest().withTargetArn(targetArn).withMessage(message);
		service.publish(publishReq);
		
		return Answer.ok();
	}

}
