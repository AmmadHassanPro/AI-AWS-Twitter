package vasco.da.gama.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.BatchDetectEntitiesItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectEntitiesResult;
import com.amazonaws.services.comprehend.model.BatchDetectKeyPhrasesItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentItemResult;
import com.amazonaws.services.comprehend.model.BatchDetectSentimentResult;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.comprehend.model.KeyPhrase;
import com.amazonaws.services.comprehend.model.SentimentScore;

import vasco.da.gama.exceptions.ServiceException;

@Component
public class ServiceUtil {
	
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
	
	public DetectSentimentResult calcOverallSentiment(BatchDetectSentimentResult batchResult) {
		DetectSentimentResult overallSentiment = new DetectSentimentResult()
																.withSentimentScore(new SentimentScore());
		Float totPositive = new Float(0.00);
		Float totNeutral = new Float(0.00);
		Float totMixed = new Float(0.00);
		Float totNegative = new Float(0.00);
		
		Iterator<BatchDetectSentimentItemResult> resultIt = batchResult.getResultList().iterator();
		while (resultIt.hasNext()) {
			BatchDetectSentimentItemResult curResult = resultIt.next();
			totPositive += curResult.getSentimentScore().getPositive();
			totNeutral += curResult.getSentimentScore().getNeutral();
			totMixed += curResult.getSentimentScore().getMixed();
			totNegative += curResult.getSentimentScore().getNegative();
		}
		
		Float numResults = new Float(batchResult.getResultList().size());
		overallSentiment.getSentimentScore().setPositive(totPositive/numResults);
		overallSentiment.getSentimentScore().setNeutral(totNeutral/numResults);
		overallSentiment.getSentimentScore().setMixed(totMixed/numResults);
		overallSentiment.getSentimentScore().setNegative(totNegative/numResults);
		
		Float highest = overallSentiment.getSentimentScore().getPositive();
		String sentiment = "POSITIVE";
		if (highest < overallSentiment.getSentimentScore().getNeutral()) {
			highest = overallSentiment.getSentimentScore().getNeutral();
			sentiment = "NEUTRAL";
		}
		if (highest < overallSentiment.getSentimentScore().getMixed()) {
			highest = overallSentiment.getSentimentScore().getMixed();
			sentiment = "MIXED";
		}
		if (highest < overallSentiment.getSentimentScore().getNegative()) {
			sentiment = "NEGATIVE";
		}
		
		overallSentiment.setSentiment(sentiment);
		
		return overallSentiment;
	}
	
	public DetectEntitiesResult calcOverallEntities(BatchDetectEntitiesResult batchResult) {
		DetectEntitiesResult overallEntities = new DetectEntitiesResult()
															.withEntities(new ArrayList<Entity>());
		
		for (BatchDetectEntitiesItemResult resultEntity : batchResult.getResultList()) {
			overallEntities.getEntities().addAll(resultEntity.getEntities());
		}
		
		return overallEntities;
	}
	
	public DetectKeyPhrasesResult calcOverallKeyPhrases(BatchDetectKeyPhrasesResult batchResult) {
		DetectKeyPhrasesResult overallKeyPhrases = new DetectKeyPhrasesResult()
															.withKeyPhrases(new ArrayList<KeyPhrase>());
		
		for (BatchDetectKeyPhrasesItemResult resultKeyPhrase : batchResult.getResultList()) {
			overallKeyPhrases.getKeyPhrases().addAll(resultKeyPhrase.getKeyPhrases());
		}
		
		return overallKeyPhrases;
	}
}
