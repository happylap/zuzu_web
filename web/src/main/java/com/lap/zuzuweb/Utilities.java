package com.lap.zuzuweb;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utilities {

	private static final Logger logger = LoggerFactory.getLogger(Utilities.class);
	
	private static SecureRandom RANDOM = new SecureRandom();
    static {
        RANDOM.generateSeed(16);
    }
    
	public static String sign(String content, String key) {
        try {
            byte[] data = content.getBytes(Constants.ENCODING_FORMAT);
            Mac mac = Mac.getInstance(Constants.SIGNATURE_METHOD);
            mac.init(new SecretKeySpec(key.getBytes(Constants.ENCODING_FORMAT), Constants.SIGNATURE_METHOD));
            char[] signature = Hex.encodeHex(mac.doFinal(data));
            return new String(signature);
        } catch (Exception e) {
        	logger.error("Exception during sign", e);
        }
        return null;
    }
	

	public static String getSaltedPassword(String email, String password) {
        return sign(password, email);
    }
	
	public static String base64(String data) throws UnsupportedEncodingException {
        byte[] signature = Base64.encodeBase64(data.getBytes(Constants.ENCODING_FORMAT));
        return new String(signature, Constants.ENCODING_FORMAT);
    }
	

    public static String generateRandomString() {
        byte[] randomBytes = new byte[16];
        RANDOM.nextBytes(randomBytes);
        String randomString = new String(Hex.encodeHex(randomBytes));
        return randomString;
    }
    
	public static boolean isValidEmail(String email) {
        return email != null && email.length() > 0;
    }
	
	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		}
		
        int length = password.length();
        return (length >= 8 && length <= 128);
    }
	
	/**
     * This method is low performance string comparison function. The purpose of
     * this method is to prevent timing attack.
     */
    public static boolean slowStringComparison(String givenSignature, String computedSignature) {
        if (null == givenSignature || null == computedSignature
                || givenSignature.length() != computedSignature.length())
            return false;

        int n = computedSignature.length();
        boolean signaturesMatch = true;

        for (int i = 0; i < n; i++) {
            signaturesMatch &= (computedSignature.charAt(i) == givenSignature.charAt(i));
        }

        return signaturesMatch;
    }
	
}
