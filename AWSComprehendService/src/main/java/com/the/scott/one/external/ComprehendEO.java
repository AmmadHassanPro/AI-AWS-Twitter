package com.the.scott.one.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.the.scott.one.exceptions.ServiceException;
import com.the.scott.one.utils.ServiceConstants;

@Component
public class ComprehendEO {
	private static final Logger LOG = LoggerFactory.getLogger(ComprehendEO.class);

	public DetectSentimentResult getSentiment(String input) throws ServiceException {
		LOG.info("Enter getSentiment");
		
		if (System.getProperty(ServiceConstants.AMAZON_API_KEY_KEY) == null || 
				System.getProperty(ServiceConstants.AMAZON_API_SECRET_KEY) == null ||
				System.getProperty(ServiceConstants.AMAZON_API_KEY_KEY).isEmpty() ||
				System.getProperty(ServiceConstants.AMAZON_API_SECRET_KEY).isEmpty()) {
			System.setProperty(ServiceConstants.AMAZON_API_KEY_KEY, ServiceConstants.AMAZON_API_KEY);
			System.setProperty(ServiceConstants.AMAZON_API_SECRET_KEY, ServiceConstants.AMAZON_API_SECRET);
		}
		
		AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
		 
        AmazonComprehend comprehendClient =
        AmazonComprehendClientBuilder.standard()
                                     .withCredentials(awsCreds)
                                     .withRegion(Regions.US_EAST_1)
                                     .build();
                                         
        // Call detectSentiment API
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(input)
                                                                                    .withLanguageCode("en");
        
        LOG.info("Exit getSentiment");
        return comprehendClient.detectSentiment(detectSentimentRequest);
	}
}
