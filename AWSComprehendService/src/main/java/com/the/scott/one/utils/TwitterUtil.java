package com.the.scott.one.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.the.scott.one.exceptions.ServiceException;

public class TwitterUtil {
	
	@Autowired
	ServiceUtil serviceUtil;
	
	String oauth_signature_method = "HMAC-SHA1";
	
	// the parameter string must be in alphabetical order
	public String generateRequestParams(String req, String id, String authToken) {

		switch (req) {
			case ServiceConstants.TWITTER_REQUEST_RTOKEN:
				String rtoken_parameter_string = "oauth_callback=oob" 
						+ "&oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
						+ "&oauth_nonce=" + serviceUtil.generateNonce() 
						+ "&oauth_signature_method=" + oauth_signature_method 
						+ "&oauth_timestamp=" + serviceUtil.generateTimestamp() 
						+ "&oauth_version=1.0";	
				return rtoken_parameter_string;
				
			case ServiceConstants.TWITTER_REQUEST_ATOKEN:
				String atoken_parameter_string = "oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
						+ "&oauth_nonce=" + serviceUtil.generateNonce() 
						+ "&oauth_signature_method=" + oauth_signature_method 
						+ "&oauth_timestamp=" + serviceUtil.generateTimestamp()  
						+ "&oauth_token=" + serviceUtil.encode(authToken) 
						+ "&oauth_version=1.0";
				return atoken_parameter_string;
				
			case ServiceConstants.TWITTER_REQUEST_ALLSEARCH:
				String allsearch_parameter_string = "id=" + id 
						+ "&oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
						+ "&oauth_nonce=" + serviceUtil.generateNonce()
						+ "&oauth_signature_method=" + oauth_signature_method 
						+ "&oauth_timestamp=" + serviceUtil.generateTimestamp()
						+ "&oauth_token=" + serviceUtil.encode(ServiceConstants.TWITTER_ACCESS_TOKEN) 
						+ "&oauth_version=1.0";
				return allsearch_parameter_string;
		}
		
		return null;
	}
	
	public String generateHttpRequestType(String req) {
		switch (req) {
			case ServiceConstants.TWITTER_REQUEST_RTOKEN:
				return "POST";
			
			case ServiceConstants.TWITTER_REQUEST_ATOKEN:
				return "POST";
			
			case ServiceConstants.TWITTER_REQUEST_ALLSEARCH:
				return "GET";
		}
		
		return null;
	}
	
	public String generateEndpoint(String req) {
		switch (req) {
		case ServiceConstants.TWITTER_REQUEST_RTOKEN:
			return ServiceConstants.TWITTER_REQ_TOKEN_ENDPOINT;
		
		case ServiceConstants.TWITTER_REQUEST_ATOKEN:
			return ServiceConstants.TWITTER_ACCESS_TOKEN_ENDPOINT;
		
		case ServiceConstants.TWITTER_REQUEST_ALLSEARCH:
			return ServiceConstants.TWITTER_ALL_SEARCH_ENDPOINT;
		}
		
		return null;
	}
	
	public String generateAuthorizationHeader(String reqType, String id, String authToken) throws ServiceException {
		String httpMethod = generateHttpRequestType(reqType);
		String oauth_nonce = serviceUtil.generateNonce();
		String oauth_timestamp = serviceUtil.generateTimestamp();
		String parameter_string = generateRequestParams(reqType, id, authToken);
		String endpoint = generateEndpoint(reqType);
		
		String signature_base_string = httpMethod + "&"+ serviceUtil.encode(endpoint) + "&" + serviceUtil.encode(parameter_string);
		
		// this time the base string is signed using twitter_consumer_secret + "&" + encode(oauth_token_secret) instead of just twitter_consumer_secret + "&"
		String oauth_signature = "";
		try {
			oauth_signature = serviceUtil.computeSignature(signature_base_string, 
														   ServiceConstants.TWITTER_CONSUMER_SECRET + "&" 
														 + serviceUtil.encode(ServiceConstants.TWITTER_ACCESS_SECRET));  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
		} catch (Exception e) {
			throw serviceUtil.createServiceException(ServiceConstants.E100, "Exception while generating twitter API call signature: " + e.getMessage());
		}
		
		String authorization_header_string = "OAuth oauth_consumer_key=\"" + ServiceConstants.TWITTER_CONSUMER_KEY 
										   + "\",oauth_signature_method=\"HMAC-SHA1\"" 
										   + ",oauth_timestamp=\"" + oauth_timestamp 
										   + "\",oauth_nonce=\"" + oauth_nonce 
										   + "\",oauth_version=\"1.0\"" 
										   + ",oauth_signature=\"" + serviceUtil.encode(oauth_signature) 
										   + "\",oauth_token=\"" + serviceUtil.encode(ServiceConstants.TWITTER_ACCESS_TOKEN) + "\"";
		return authorization_header_string;
	}
}
