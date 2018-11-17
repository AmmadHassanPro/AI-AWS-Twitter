package com.the.scott.one.utils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.the.scott.one.exceptions.ServiceException;

public class ServiceUtil {
	public String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	System.out.println("Exception occured while encoding! " + e.getMessage());
        }
        
        StringBuilder buf = new StringBuilder(encoded.length());
        
        char focus;
        
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
             /* Java's native URLEncoder.encode function will not work. 
              * It is the wrong RFC specification (which does "+" where "%20" should be)...
              */ 
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
	
	public String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {
	    SecretKey secretKey = null;

	    byte[] keyBytes = keyString.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");
	    mac.init(secretKey);

	    byte[] text = baseString.getBytes();

	    return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}
	
	public ServiceException createServiceException(String errorCode, String message) {
		ServiceException ret = new ServiceException();
		ret.setErrorCode(errorCode);
		ret.setMessage(message);
		return ret;
	}
	
	public String generateNonce() {
		String uuid_string = UUID.randomUUID().toString();
		return uuid_string.replaceAll("-", "");
	}
	
	public String generateTimestamp() {
		Calendar tempcal = Calendar.getInstance();
		long ts = tempcal.getTimeInMillis();// get current time in milliseconds
		return (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds
	}
}
