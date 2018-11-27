package vasco.da.gama.external;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vasco.da.gama.exceptions.ServiceException;
import vasco.da.gama.utils.ServiceConstants;
import vasco.da.gama.utils.ServiceUtil;
@Component
public class TwitterEO {
	private static final Logger LOG = LoggerFactory.getLogger(TwitterEO.class);
	
	@Autowired
	ServiceUtil serviceUtil = new ServiceUtil();

	/**
	 * Searches Twitter for the given ID and returns an ArrayList of the tweet bodies
	 * 
	 * @param id
	 * @return ArrayList<String> tweets
	 * @throws ServiceException
	 */
	public ArrayList<String> searchTwitterForString(String id) throws ServiceException {
		LOG.info("Enter searchTwitterForString");
		
		if (ServiceConstants.TWITTER_BEARER_TOKEN == null || ServiceConstants.TWITTER_BEARER_TOKEN.isEmpty()) {
			throw new ServiceException("Missing Bearer Token for Twitter Search call", ServiceConstants.E100);
		}

		String url = ServiceConstants.TWITTER_SEARCH_ENDPOINT; 
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpGet httpGet = null; 
		Map<String,String> responseMap = new HashMap<String,String>();
		ArrayList<String> tweetText = new ArrayList<>();
		
		
		
		CloseableHttpResponse response = null;
		try {
			httpGet = new HttpGet(url + "?q=" + URLEncoder.encode(id, "UTF-8"));
			LOG.debug("Bearer token retrieved: {}", ServiceConstants.TWITTER_BEARER_TOKEN);
			httpGet.setHeader("Authorization", "Bearer " + ServiceConstants.TWITTER_BEARER_TOKEN);
			
			response = httpclient.execute(httpGet);
			
			responseMap.put(ServiceConstants.RESPONSE_STATUS, ServiceConstants.STATUS_ERROR);

			if (response != null) {
				String responseBody = EntityUtils.toString(response.getEntity());
				LOG.debug(responseBody);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode responseObj = mapper.readTree(responseBody);
				JsonNode statuses = responseObj.get("statuses");
				for (int i = 0; i < statuses.size(); i++) {
					if (statuses.get(i) != null) {
						tweetText.add(statuses.get(i).get("text").asText());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw serviceUtil.createServiceException(ServiceConstants.E100, "Error while parsing response form search");
		}
		LOG.info("Exit searchTwitterForString");
		
		return tweetText;
	}

	/**
	 * Will set the bearer token into the constants file so only call when thats empty
	 * 
	 * @throws ServiceException
	 */
	public void getBearerToken() throws ServiceException {
		LOG.info("Enter getBearerToken");
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(ServiceConstants.TWITTER_BEARER_TOKEN_ENDPOINT);
		CloseableHttpResponse response = null;
		ArrayList<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		
		try {
			String encodedKeyAndSecret = Base64.getEncoder().encodeToString(
					(URLEncoder.encode(ServiceConstants.TWITTER_CONSUMER_KEY, "UTF-8") + 
					":" + 
					 URLEncoder.encode(ServiceConstants.TWITTER_CONSUMER_SECRET, "UTF-8")).getBytes());
			
			httpPost.setHeader("Authorization", "Basic " + encodedKeyAndSecret);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			response = httpclient.execute(httpPost);
			
			if (response != null) {
				String responseBody = EntityUtils.toString(response.getEntity());
				ObjectMapper mapper = new ObjectMapper();
				JsonNode responseObj = mapper.readTree(responseBody);
				String bearerToken = responseObj.get("access_token").toString();
				bearerToken = bearerToken.replace("\"", "");
				LOG.info("Storing Bearer Token {}", bearerToken);
				ServiceConstants.TWITTER_BEARER_TOKEN = bearerToken;
			}
		} catch (Exception e) {
			throw new ServiceException("Exception occured while fetching Twitter Bearer Token", ServiceConstants.E100);
		}
		LOG.info("Exit getBearerToken");
	}
}
