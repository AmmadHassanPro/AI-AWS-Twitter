package com.the.scott.one.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.the.scott.one.exceptions.ServiceException;

public class ServiceAuthUtil {
	
	private static ServiceUtil utils = new ServiceUtil();
	
	// Method from: https://github.com/cyrusadkisson/twitter_api_examples
	public static Map<String,String> getTwitterAuthToken() throws ServiceException {
			
		String post = "POST";
		String oauth_signature_method = "HMAC-SHA1";
		
		String uuid_string = UUID.randomUUID().toString();
		uuid_string = uuid_string.replaceAll("-", "");
		String oauth_nonce = uuid_string;
		
		Calendar tempcal = Calendar.getInstance();
		long ts = tempcal.getTimeInMillis();
		String oauth_timestamp = (new Long(ts/1000)).toString(); // divide by 1000 to get seconds
		
		// assemble the proper parameter string, which must be in alphabetical order, using your consumer key
		String parameter_string = "oauth_callback=oob" 
								+ "&oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
								+ "&oauth_nonce=" + oauth_nonce 
								+ "&oauth_signature_method=" + oauth_signature_method 
								+ "&oauth_timestamp=" + oauth_timestamp 
								+ "&oauth_version=1.0";		
		
		// specify the proper twitter API endpoint at which to direct this request
		String twitter_req_token_endpoint = "https://api.twitter.com/oauth/request_token";
		
		// assemble the string to be signed. It goes 'METHOD & (percent-encoded) endpoint & (percent-encoded) parameter_string'
		// the encode() function included in this class compensates to conform to RFC 3986 (which twitter requires)
		String signature_base_string = post + "&"+ utils.encode(twitter_req_token_endpoint) + "&" + utils.encode(parameter_string);
		
		// now that we've got the string we want to sign (see directly above) HmacSHA1 hash it against the consumer secret
		String oauth_signature = "";
		try {
			oauth_signature = utils.computeSignature(signature_base_string, ServiceConstants.TWITTER_CONSUMER_SECRET + "&");  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception while computing signature: " + e.getMessage());
		}
		
		// each request to the twitter API 1.1 requires an Authorization header
		String authorization_header_string = "OAuth oauth_callback=\"oob\"," 
										   + "oauth_consumer_key=\"" + ServiceConstants.TWITTER_CONSUMER_KEY 
										   + "\",oauth_signature_method=\"HMAC-SHA1\"," 
										   + "oauth_timestamp=\"" + oauth_timestamp 
										   + "\",oauth_nonce=\"" + oauth_nonce 
										   + "\",oauth_version=\"1.0\"," 
										   + "oauth_signature=\"" + utils.encode(oauth_signature) + "\"";
		     
		String url = twitter_req_token_endpoint; 
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpPost httpPost = null; 
		httpPost = new HttpPost(url);

		List<NameValuePair> headers = new ArrayList<NameValuePair>();	 										// create an empty list
		NameValuePair authorizationPair = new BasicNameValuePair("Authorization", authorization_header_string);	// add the "Authorization" header with the string created above.
		headers.add(authorizationPair);																			// add the pair to the list of headers
		
		// now loop the header pairs and add them to the post
		NameValuePair current = null;
		Iterator<NameValuePair> header_it = headers.iterator();
		while(header_it.hasNext())
		{
			current = header_it.next();
			httpPost.setHeader(current.getName(), current.getValue());
		}
		
		//Send the Request
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception occured while fetching twitter request token: " + e.getMessage());
		} 
		
		Map<String,String> responseMap = new HashMap<String,String>();
		responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_ERROR);
		responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "Response was not HTTP 200 OK");
				
		try {
			String responseBody = EntityUtils.toString(response.getEntity());
			
			if(response.getStatusLine().getStatusCode() != 200) {
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "Twitter request_token request failed: " + response.getStatusLine().getStatusCode());
				return responseMap;
			}
				
			// if this were true, that would be weird. Successful (200) response, but no oauth_callback_confirmed? 
			if(responseBody.indexOf("oauth_callback_confirmed=") == -1) {
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "Twitter request_token request failed. response was 200 but did not contain oauth_callback_confirmed");
				return responseMap;
			}
			
			String oauth_token = "";
			String oauth_token_secret = "";
			// using the tokenizer takes away the need for the values to be in any particular order.
			StringTokenizer st = new StringTokenizer(responseBody, "&");
			String currenttoken = "";
			
			while(st.hasMoreTokens()) {
				currenttoken = st.nextToken();
				if(currenttoken.startsWith("oauth_token="))
					oauth_token = currenttoken.substring(currenttoken.indexOf("=") + 1);
				else if(currenttoken.startsWith("oauth_token_secret="))
					oauth_token_secret = currenttoken.substring(currenttoken.indexOf("=") + 1);
				else if(currenttoken.startsWith("oauth_callback_confirmed=")) {
					//oauth_callback_confirmed = currenttoken.substring(currenttoken.indexOf("=") + 1);
				}
				else {
					throw utils.createServiceException(ServiceConstants.E100, "Warning... twitter returned a key we weren't looking for.");
				}
			}
			
			if(oauth_token.equals("") || oauth_token_secret.equals("")) { // if either key is empty, that's weird and bad
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "oauth tokens in response were invalid");
				return responseMap;
			}
			
			responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_OK);
			responseMap.put(ServiceConstants.RESPONSE_MESSAGE, oauth_token);
			
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception in reading Resquest Token response: " + e.getMessage());
		}
		return responseMap;
	}
	
	// Method from: https://github.com/cyrusadkisson/twitter_api_examples
	public static Map<String,String> getTwitterAccessToken(String authToken, String pin) throws ServiceException {

		String post = "POST";
		String oauth_signature_method = "HMAC-SHA1";
		
		String uuid_string = UUID.randomUUID().toString();
		uuid_string = uuid_string.replaceAll("-", "");
		String oauth_nonce = uuid_string;
		
		// get the timestamp
		Calendar tempcal = Calendar.getInstance();
		long ts = tempcal.getTimeInMillis();
		String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds
		
		
		// the parameter string must be in alphabetical order
		String parameter_string = "oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
								+ "&oauth_nonce=" + oauth_nonce 
								+ "&oauth_signature_method=" + oauth_signature_method 
								+ "&oauth_timestamp=" + oauth_timestamp 
								+ "&oauth_token=" + utils.encode(authToken) 
								+ "&oauth_version=1.0";		
		
		System.out.println("parameter_string=" + parameter_string);
		
		String twitter_acess_token_endpoint = "https://api.twitter.com/oauth/access_token";
		String signature_base_string = post + "&"+ utils.encode(twitter_acess_token_endpoint) + "&" + utils.encode(parameter_string);
			
		String oauth_signature = "";
		try {
			oauth_signature = utils.computeSignature(signature_base_string, ServiceConstants.TWITTER_CONSUMER_SECRET + "&");  // note the & at the end. Normally the user access_token would go here, but we don't know it yet
			System.out.println("oauth_signature=" + utils.encode(oauth_signature));
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception occured while computing Access Token Signature: " + e.getMessage());
		}
		
		String authorization_header_string = "OAuth oauth_consumer_key=\"" + ServiceConstants.TWITTER_CONSUMER_KEY 
										   + "\",oauth_signature_method=\"HMAC-SHA1\"" 
										   + ",oauth_timestamp=\"" + oauth_timestamp 
										   + "\",oauth_nonce=\"" + oauth_nonce 
										   + "\",oauth_version=\"1.0\"" 
										   + ",oauth_signature=\"" + utils.encode(oauth_signature) 
										   + "\",oauth_token=\"" + utils.encode(authToken) + "\"";
		    
		String url = twitter_acess_token_endpoint; 
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpPost httpPost = null; 
		httpPost = new HttpPost(url);

		List<NameValuePair> headers = new ArrayList<NameValuePair>();	 										// create an empty list
		NameValuePair authorizationPair = new BasicNameValuePair("Authorization", authorization_header_string);	// add the "Authorization" header with the string created above.
		headers.add(authorizationPair);																			// add the pair to the list of headers
		
		// now loop the header pairs and add them to the post
		NameValuePair current = null;
		Iterator<NameValuePair> header_it = headers.iterator();
		while(header_it.hasNext())
		{
			current = header_it.next();
			System.out.println("Adding header " + current.getName() + ":" + current.getValue());
			httpPost.setHeader(current.getName(), current.getValue());
		}

		List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
		NameValuePair verifierParam = new BasicNameValuePair("oauth_verifier", utils.encode(pin));
		bodyParams.add(verifierParam);
		
		UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(bodyParams,StandardCharsets.UTF_8);
		try {
			System.out.println("Body entity: " + EntityUtils.toString(uefe));
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception occured while encoding Access Token request params: " + e.getMessage());
		}
		httpPost.setEntity(uefe);
		
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception occured while requesting Access Token: " + e.getMessage());
		}
		
		Map<String,String> responseMap = new HashMap<String,String>();
		responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_ERROR);
		responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "Default error message for generate Access Token");
		
		try {
			if(response.getStatusLine().getStatusCode() != 200) {
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "Twitter Access Token request failed: " + response.getStatusLine().getStatusCode());
				return responseMap;
			}
			
			String responseBody = EntityUtils.toString(response.getEntity());
			
			String oauth_token = "";
			String oauth_token_secret = "";
			// using the tokenizer takes away the need for the values to be in any particular order.
			StringTokenizer st = new StringTokenizer(responseBody, "&");
			String currenttoken = "";
			
			while(st.hasMoreTokens()) {
				currenttoken = st.nextToken();
				if(currenttoken.startsWith("oauth_token="))
					oauth_token = currenttoken.substring(currenttoken.indexOf("=") + 1);
				else if(currenttoken.startsWith("oauth_token_secret="))
					oauth_token_secret = currenttoken.substring(currenttoken.indexOf("=") + 1);
				else if(currenttoken.startsWith("oauth_callback_confirmed=")) {
					//oauth_callback_confirmed = currenttoken.substring(currenttoken.indexOf("=") + 1);
				}
			}
			
			if(oauth_token.equals("") || oauth_token_secret.equals("")) { // if either key is empty, that's weird and bad
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "oauth tokens in response were invalid");
				return responseMap;
			}
			
			responseMap.put(ServiceConstants.RESPONSE_MESSAGE, oauth_token+","+oauth_token_secret);
			responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_OK);
			
		} catch (Exception e) {
			throw utils.createServiceException(ServiceConstants.E100, "Exception occured while reading Access Token response: " + e.getMessage());
		}
		
		return responseMap;
	}

}
