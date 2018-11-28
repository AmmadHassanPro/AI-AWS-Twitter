package vasco.da.gama.external;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesResult;
import com.amazonaws.services.comprehend.model.BatchDetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.BatchDetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentRequest;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentResult;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import vasco.da.gama.exceptions.ServiceException;
import vasco.da.gama.utils.ServiceConstants;

@Component
public class ComprehendEO {
	private static final Logger LOG = LoggerFactory.getLogger(ComprehendEO.class);

	public DetectSentimentResult getSingleSentiment(String input) throws ServiceException {
		LOG.info("Enter getSentiment");
		
		AmazonComprehend comprehendClient = getComprehendClient();
                                         
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(input)
                                                                                    .withLanguageCode(ServiceConstants.LANG);
        
        LOG.info("Exit getSentiment");
        return comprehendClient.detectSentiment(detectSentimentRequest);
	}
	
	public DetectEntitiesResult getSingleEntity(String input) throws ServiceException {
		AmazonComprehend comprehendClient = getComprehendClient();
		
		DetectEntitiesRequest entityRequest = new DetectEntitiesRequest()
																.withText(input)
																.withLanguageCode(ServiceConstants.LANG);
		
		return comprehendClient.detectEntities(entityRequest);
	}

	public DetectKeyPhrasesResult getSingleKeyPhrase(String input) {
		AmazonComprehend comprehendClient = getComprehendClient();
		
		DetectKeyPhrasesRequest entityRequest = new DetectKeyPhrasesRequest()
																.withText(input)
																.withLanguageCode(ServiceConstants.LANG);
		
		return comprehendClient.detectKeyPhrases(entityRequest);
	}
	
	public BatchDetectSentimentResult getBatchSentiment(ArrayList<String> tweets) throws ServiceException {
		LOG.info("Enter getSentiment");
		
		AmazonComprehend comprehendClient = getComprehendClient();
                                         
        BatchDetectSentimentRequest batchRequest = new BatchDetectSentimentRequest().withTextList(tweets)
        																			.withLanguageCode(ServiceConstants.LANG);
        
        LOG.info("Exit getSentiment");
        return comprehendClient.batchDetectSentiment(batchRequest);
	}

	public BatchDetectEntitiesResult getBatchEntities(ArrayList<String> tweets) throws ServiceException {
		
		AmazonComprehend comprehendClient = getComprehendClient();
		
        BatchDetectEntitiesRequest batchRequest = new BatchDetectEntitiesRequest()
        															.withTextList(tweets)
        															.withLanguageCode(ServiceConstants.LANG);
        
        return comprehendClient.batchDetectEntities(batchRequest);
	}
	
	public BatchDetectKeyPhrasesResult getBatchKeyPhrases(ArrayList<String> tweets) throws ServiceException {
		AmazonComprehend comprehendClient = getComprehendClient();
		
		BatchDetectKeyPhrasesRequest batchRequest = new BatchDetectKeyPhrasesRequest()
																		.withTextList(tweets)
																		.withLanguageCode(ServiceConstants.LANG);
		
		return comprehendClient.batchDetectKeyPhrases(batchRequest);
	}
	
	private AmazonComprehend getComprehendClient() {
		if (System.getProperty(ServiceConstants.AMAZON_API_KEY_KEY) == null || 
				System.getProperty(ServiceConstants.AMAZON_API_SECRET_KEY) == null ||
				System.getProperty(ServiceConstants.AMAZON_API_KEY_KEY).isEmpty() ||
				System.getProperty(ServiceConstants.AMAZON_API_SECRET_KEY).isEmpty()) {
			System.setProperty(ServiceConstants.AMAZON_API_KEY_KEY, ServiceConstants.AMAZON_API_KEY);
			System.setProperty(ServiceConstants.AMAZON_API_SECRET_KEY, ServiceConstants.AMAZON_API_SECRET);
		}
		
		AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
		 
        return AmazonComprehendClientBuilder.standard()
                                     		.withCredentials(awsCreds)
                                     		.withRegion(Regions.US_EAST_1)
                                     		.build();
	}
}
