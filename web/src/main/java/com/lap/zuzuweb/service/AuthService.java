package com.lap.zuzuweb.service;

import java.io.InputStream;

public interface AuthService {

	public boolean isSuperTokenValid(String token);
	public boolean isBasicTokenValid(String token);
	public boolean isPurchaseReceiptValid(InputStream purchaseReceipt) throws Exception;
	public boolean isGoogleTokenValid(String idTokenString) throws Exception;
	public boolean isFacebookTokenValid(String token) throws Exception;
}
