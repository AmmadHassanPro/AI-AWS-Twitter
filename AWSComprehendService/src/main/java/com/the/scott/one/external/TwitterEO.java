package com.the.scott.one.external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.the.scott.one.exceptions.ServiceException;
import com.the.scott.one.utils.ServiceConstants;
import com.the.scott.one.utils.ServiceUtil;
import com.the.scott.one.utils.TwitterUtil;

public class TwitterEO {
	@Autowired
	static
	ServiceUtil serviceUtil = new ServiceUtil();
	
	@Autowired
	TwitterUtil twitterUtil;
	
	public Map<String,String> callTwitter(String reqType, String id, String authToken) throws ServiceException {
		// Generate Header String
		String httpMethod = twitterUtil.generateHttpRequestType(reqType);
		String oauth_nonce = serviceUtil.generateNonce();
		String oauth_timestamp = serviceUtil.generateTimestamp();
		String parameter_string = twitterUtil.generateRequestParams(reqType, id, authToken);
		String endpoint = twitterUtil.generateEndpoint(reqType);
		
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
		    
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpGet httpGet = null; 
		httpGet = new HttpGet(endpoint + "?id=" + id);

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
			httpGet.setHeader(current.getName(), current.getValue());
		}

		// print request as we have it so far
		System.out.println("\n**************");
		System.out.println(httpGet.getRequestLine());
		Header[] headers2 = httpGet.getAllHeaders();
		int h = 0;
		while(h < headers2.length)
		{
			System.out.println("name=" +headers2[h].getName() + " value=" + headers2[h].getValue());
			h++;
		}
		System.out.println("\n**************");
		
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,String> responseMap = new HashMap<String,String>();
		
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
				else {
					throw serviceUtil.createServiceException(ServiceConstants.E100, "Warning... twitter returned a key we weren't looking for.");
				}
			}
			
			if(oauth_token.equals("") || oauth_token_secret.equals("")) { // if either key is empty, that's weird and bad
				responseMap.put(ServiceConstants.RESPONSE_MESSAGE, "oauth tokens in response were invalid");
				return responseMap;
			}
			
			responseMap.put(ServiceConstants.RESPONSE_MESSAGE, responseBody);
			responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_OK);
			
		} catch (Exception e) {
			throw serviceUtil.createServiceException(ServiceConstants.E100, "Exception occured while reading Access Token response: " + e.getMessage());
		}
		
		
		
		
		return responseMap;
	}

	public static Map<String,String> searchTwitterForString(String id, String accessToken, String accessTokenSecret) throws ServiceException {
		System.out.println("Enter searchTwitterForString");
		// generate authorization header
		String get = "GET";
		String oauth_signature_method = "HMAC-SHA1";
		System.out.println("Before computing signature1");
		String oauth_nonce = serviceUtil.generateNonce(); // any relatively random alphanumeric string will work here
		System.out.println("Before computing signature2");
		String oauth_timestamp = serviceUtil.generateTimestamp(); // then divide by 1000 to get seconds
		System.out.println("Before computing signature3");
		// the parameter string must be in alphabetical order
		// this time, I add 3 extra params to the request, "lang", "result_type" and "q".
		String parameter_string = "id=" + id 
								+ "&oauth_consumer_key=" + ServiceConstants.TWITTER_CONSUMER_KEY 
								+ "&oauth_nonce=" + oauth_nonce 
								+ "&oauth_signature_method=" + oauth_signature_method 
								+ "&oauth_timestamp=" + oauth_timestamp 
								+ "&oauth_token=" + serviceUtil.encode(accessToken) 
								+ "&oauth_version=1.0";
		System.out.println("Before computing signature4");
		String twitter_endpoint = "https://api.twitter.com/1.1/search/tweets.json";
		System.out.println("Before computing signature5");
		String signature_base_string = get + "&"
									 + serviceUtil.encode(twitter_endpoint) + "&" 
									 + serviceUtil.encode(parameter_string);
		System.out.println("Before computing signature6");
		// this time the base string is signed using twitter_consumer_secret + "&" + encode(oauth_token_secret) instead of just twitter_consumer_secret + "&"
		String oauth_signature = "";
		try {
			oauth_signature = serviceUtil.computeSignature(signature_base_string, 
					ServiceConstants.TWITTER_CONSUMER_KEY  + "&" + serviceUtil.encode(accessTokenSecret));  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
		} catch (Exception e) {
			throw serviceUtil.createServiceException(ServiceConstants.E100, "service exception while computing signature: " + e.getMessage());
		}
		
		String authorization_header_string = "OAuth oauth_consumer_key=\"" + ServiceConstants.TWITTER_CONSUMER_KEY 
										   + "\",oauth_signature_method=\"HMAC-SHA1\"," 
										   + "oauth_timestamp=\"" + oauth_timestamp 
										   + "\",oauth_nonce=\"" + oauth_nonce 
										   + "\",oauth_version=\"1.0\"," 
										   + "oauth_signature=\"" + serviceUtil.encode(oauth_signature) 
										   + "\",oauth_token=\"" + serviceUtil.encode(accessToken) + "\"";
		    
		String url = twitter_endpoint; 
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpGet httpGet = null; 
		httpGet = new HttpGet(url + "?id=" + id);

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
			httpGet.setHeader(current.getName(), current.getValue());
		}

		// print request as we have it so far
		System.out.println("\n**************");
		System.out.println(httpGet.getRequestLine());
		Header[] headers2 = httpGet.getAllHeaders();
		int h = 0;
		while(h < headers2.length)
		{
			System.out.println("name=" +headers2[h].getName() + " value=" + headers2[h].getValue());
			h++;
		}
		System.out.println("\n**************");
		
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,String> responseMap = new HashMap<String,String>();
		responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_ERROR);
		
		try {
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println("Raw response: " + response.toString());
			responseMap.put(ServiceConstants.RESPONSE_MESSAGE, responseBody);
			responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_OK);
		} catch (Exception e) {
			throw serviceUtil.createServiceException(ServiceConstants.E100, "Error while parsing response form search");
		}
		
		return responseMap;
	}
}
