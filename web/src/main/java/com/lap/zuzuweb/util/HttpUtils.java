package com.lap.zuzuweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtils {

	public static String get(String url) throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);

		StringBuffer result = new StringBuffer();
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}
		} finally {
			response.close();
		}

		return result.toString();
	}

	public static String post(String url, HttpEntity entity) throws IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(url);
		// httpPost.addHeader("User-Agent", USER_AGENT);
		
		httpPost.setEntity(entity);

		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

		//System.out.println("POST Response Status:: " + httpResponse.getStatusLine().getStatusCode());

		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}
		reader.close();
		//System.out.println(response.toString());
		httpClient.close();

		return response.toString();
	}

	public static void main(String[] args) throws Exception {
		String url = "https://graph.facebook.com/debug_token?input_token=CAAOxNzGZCZCPQBAAw2GW2W9gESJ2DYk8Rmb0OH9C9QIKehuVUk4yuZABZBC6qZBjIYV8JTqMttbcmi4TFAYjGmVbZA7dslSC5GePUAena33krpInMxXrPcgju2ZAftdf3VWuEAwEpxsgMhPeVtLXW41J6BkDGcZAJxhQa3Auyy994MAekqr0aAYFZCpfQYfFlKZCvflrU7Ibnm5vgXC2Arb8IZA8H3zDSWp6HIdEavawtMgwwZDZD&access_token=CAAOxNzGZCZCPQBAAw2GW2W9gESJ2DYk8Rmb0OH9C9QIKehuVUk4yuZABZBC6qZBjIYV8JTqMttbcmi4TFAYjGmVbZA7dslSC5GePUAena33krpInMxXrPcgju2ZAftdf3VWuEAwEpxsgMhPeVtLXW41J6BkDGcZAJxhQa3Auyy994MAekqr0aAYFZCpfQYfFlKZCvflrU7Ibnm5vgXC2Arb8IZA8H3zDSWp6HIdEavawtMgwwZDZD";
		String json = HttpUtils.get(url);

		System.out.println(json);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();

		// convert JSON string to Map
		map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		System.out.println(map);

	}
}
