package com.lap.zuzuweb;

import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.Endpoint;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.PublishRequest;

public class AmazonSNSClientTest {

	private String ACCESS_KEY = Secrets.AWS_ACCESS_KEY_ID;
	private String SECRET_KEY = Secrets.AWS_SECRET_ACCESS_KEY;
	private String REGION = Secrets.AWS_COGNITO_REGION;

	@Test
	public void sendMessageViaSNS() {
		try {
			// Create a client
			AmazonSNSClient service = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
			service.setRegion(RegionUtils.getRegion(REGION));

			String targetArn = "arn:aws:sns:ap-northeast-1:994273935857:endpoint/APNS/zuzurentals/25ba882c-b3d0-3a20-9650-b000ccf2e5ee";
			//String targetArn2 = "arn:aws:sns:ap-northeast-1:994273935857:endpoint/APNS/zuzurentals/4a35fcf3-1a13-34ac-9194-57f4dff47213";
			
			PublishRequest publishReq = new PublishRequest().withTargetArn(targetArn)
					.withMessage("Example notification sent at " + new Date());
			service.publish(publishReq);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		try {
			// Create a client
			AmazonSNSClient service = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
			service.setRegion(RegionUtils.getRegion(REGION));

			String endpointArn = "arn:aws:sns:ap-northeast-1:994273935857:endpoint/APNS/zuzurentals/4a35fcf3-1a13-34ac-9194-57f4dff47213";
			GetEndpointAttributesRequest geaReq = new GetEndpointAttributesRequest().withEndpointArn(endpointArn);
			GetEndpointAttributesResult geaRes = service.getEndpointAttributes(geaReq);

			System.out.println("Token: " + geaRes.getAttributes().get("Token"));
			System.out.println("User Data: " + geaRes.getAttributes().get("CustomUserData"));
			System.out.println("Enabled: " + geaRes.getAttributes().get("Enabled"));

			String targetToken = "3a50b0e8fc13fa0d4bc34c66669eb6a93d824ef3df7777d9a601b3df8a39638c";

			String platformApplicationArn = "arn:aws:sns:ap-northeast-1:994273935857:app/APNS/zuzurentals";

			ListEndpointsByPlatformApplicationRequest leReq = new ListEndpointsByPlatformApplicationRequest();
			leReq.setPlatformApplicationArn(platformApplicationArn);

			leReq.setNextToken(targetToken);
			ListEndpointsByPlatformApplicationResult leRes = service.listEndpointsByPlatformApplication(leReq);

			if (CollectionUtils.isNotEmpty(leRes.getEndpoints())) {
				Endpoint endpoint = leRes.getEndpoints().get(0);
				System.out.println(endpoint);
			}

			/*
			 * List<Endpoint> endpoints = new ArrayList<Endpoint>();
			 * 
			 * String nextToken = null;
			 * 
			 * do {
			 * 
			 * // create the request, with nextToken if not empty
			 * ListEndpointsByPlatformApplicationRequest leReq = new
			 * ListEndpointsByPlatformApplicationRequest();
			 * leReq.setPlatformApplicationArn(platformApplicationArn);
			 * 
			 * leReq = leReq.withNextToken(targetToken);
			 * ListEndpointsByPlatformApplicationResult leRes =
			 * service.listEndpointsByPlatformApplication(leReq);
			 * 
			 * if (CollectionUtils.isNotEmpty(leRes.getEndpoints())) { Endpoint
			 * endpoint = leRes.getEndpoints().get(0);
			 * System.out.println(endpoint); }
			 * 
			 * 
			 * // if (nextToken != null) leReq =
			 * leReq.withNextToken(targetToken); // // // call the web service
			 * // ListEndpointsByPlatformApplicationResult leRes =
			 * service.listEndpointsByPlatformApplication(leReq); // //
			 * nextToken = leRes.getNextToken(); // // // get that list of
			 * topics // endpoints.addAll(leRes.getEndpoints());
			 * 
			 * // go on if there are more elements } while (nextToken != null);
			 */
			/*
			 * System.out.println("endpoints: " + endpoints);
			 * System.out.println("endpoints size: " + endpoints.size());
			 * 
			 * for (Endpoint endpoint : endpoints) {
			 * 
			 * String token = endpoint.getAttributes().get("Token");
			 * 
			 * if (StringUtils.equals(targetToken, token)) {
			 * System.out.println(endpoint); }
			 * 
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
