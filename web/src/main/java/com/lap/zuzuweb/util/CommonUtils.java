package com.lap.zuzuweb.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommonUtils {

	public static Date getUTCNow() {
		return new Date();
	}
	
	//
	public static Date getUTCDateFromString(String utcTime) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.parse(utcTime);
	}
	
	public static String getUTCStringFromDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
	
	public static String toJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(data);
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }
	
	public static String decodeFromBase64String(String base64encodedString) {
		// Decode
		String decodeString = null;
		try {
			byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
			decodeString = new String(base64decodedBytes, "utf-8");
		} catch (Exception e) {
			
		}
		return decodeString;
	}
	
	public static String combineUserID(String provider, String userid) {
		return provider + userid;
	}
	
	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
	}
}


