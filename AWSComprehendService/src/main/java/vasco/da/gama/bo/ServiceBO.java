package vasco.da.gama.bo;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.KeyPhrase;

import vasco.da.gama.beans.GetOverallTwitterFeelOutput;
import vasco.da.gama.exceptions.ServiceException;
import vasco.da.gama.external.ComprehendEO;
import vasco.da.gama.external.TwitterEO;
import vasco.da.gama.utils.ServiceConstants;
import vasco.da.gama.utils.ServiceMapperUtil;
import vasco.da.gama.utils.ServiceUtil;

@Component
public class ServiceBO {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceBO.class);
	
	int loop = 0;
	
	@Autowired
	TwitterEO twitterEO;
	
	@Autowired
	ComprehendEO comprehendEO;
	
	@Autowired
	ServiceMapperUtil mapperUtil;
	
	@Autowired
	ServiceUtil serviceUtil;
	
	public GetOverallTwitterFeelOutput getOverallSentiment(String searchString) throws ServiceException {
		LOG.info("Enter getOverallSentiment");
		DetectSentimentResult overallSentiment = null;
		DetectEntitiesResult overallEntities = null;
		DetectKeyPhrasesResult overallKeyPhrases = null;
		
		try {
			ArrayList<String> tweets = twitterEO.searchTwitterForString(searchString);
			overallSentiment = serviceUtil.calcOverallSentiment(comprehendEO.getBatchSentiment(tweets));
			//overallEntities = serviceUtil.calcOverallEntities(comprehendEO.getBatchEntities(tweets));
			overallKeyPhrases = serviceUtil.calcOverallKeyPhrases(comprehendEO.getBatchKeyPhrases(tweets));
		} catch (ServiceException e) { throw e; } 
		catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception Occured in BO Layer!", ServiceConstants.E100);
		}
		
		LOG.info("Exit getOverallSentiment");
		return mapperUtil.populateOverallSentimentOutput(overallSentiment, overallEntities, overallKeyPhrases);
	}
	
	private DetectKeyPhrasesResult distillKeyPhrases(DetectKeyPhrasesResult overallKeyPhrases) {
		LOG.info("Enter distillKeyPhrases");
		StringBuilder allPhrases = new StringBuilder();
		for (KeyPhrase keyPhrase : overallKeyPhrases.getKeyPhrases()) {
			allPhrases.append(keyPhrase.getText());
			allPhrases.append(" ");
		}
		DetectKeyPhrasesResult distilledKeyPhrases = comprehendEO.getSingleKeyPhrase(allPhrases.toString());
		if (distilledKeyPhrases.getKeyPhrases().size() > 10 && loop < 4) {
			loop++;
			distillKeyPhrases(distilledKeyPhrases);
		}
		LOG.info("Exit distillKeyPhrases");
		return distilledKeyPhrases; 
	}

}
