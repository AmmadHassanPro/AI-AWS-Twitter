package the.scott.one.bo;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.SentimentScore;
import com.the.scott.one.exceptions.ServiceException;
import com.the.scott.one.external.ComprehendEO;
import com.the.scott.one.external.TwitterEO;
import com.the.scott.one.utils.ServiceConstants;
import com.the.scott.one.utils.ServiceMapperUtil;

import the.scott.one.beans.GetOverallTwitterSentimentOutput;
import the.scott.one.beans.SentimentBean;

@Component
public class ServiceBO {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceBO.class);
	
	@Autowired
	TwitterEO twitterEO;
	
	@Autowired
	ComprehendEO comprehendEO;
	
	@Autowired
	ServiceMapperUtil mapperUtil;
	
	public GetOverallTwitterSentimentOutput getOverallSentiment(String id) throws ServiceException {
		LOG.info("Enter getOverallSentiment");
		ArrayList<SentimentBean> sentimentList = new ArrayList<>();
		DetectSentimentResult overallSentiment = new DetectSentimentResult();
		SentimentScore score = new SentimentScore();
		overallSentiment.setSentimentScore(score);
		
		Float totPositive = new Float(0.00);
		Float totNeutral = new Float(0.00);
		Float totMixed = new Float(0.00);
		Float totNegative = new Float(0.00);
		
		try {
			twitterEO.getBearerToken();
			ArrayList<String> tweets = twitterEO.searchTwitterForString("@Discover");
			
			for(int i = 0; i <tweets.size(); i++) {
				String tweet = tweets.get(i);
				SentimentBean sentBean = new SentimentBean(tweet, comprehendEO.getSentiment(tweet));
				sentimentList.add(sentBean);
				totPositive += sentBean.getSentiment().getSentimentScore().getPositive();
				totNeutral += sentBean.getSentiment().getSentimentScore().getNeutral();
				totMixed += sentBean.getSentiment().getSentimentScore().getMixed();
				totNegative += sentBean.getSentiment().getSentimentScore().getNegative();
			}
			Float numTweets = new Float(tweets.size());
			Float overallPositive = totPositive/numTweets;
			Float overallNeutral = totNeutral/numTweets;
			Float overallMixed = totMixed/numTweets;
			Float overallNegative = totNegative/numTweets;
			
			overallSentiment.getSentimentScore().setPositive(overallPositive);
			overallSentiment.getSentimentScore().setNeutral(overallNeutral);
			overallSentiment.getSentimentScore().setMixed(overallMixed);
			overallSentiment.getSentimentScore().setNegative(overallNegative);
			
			Float highest = overallSentiment.getSentimentScore().getPositive();
			overallSentiment.setSentiment("POSITIVE");
			if (overallSentiment.getSentimentScore().getNeutral() > highest) {
				highest = overallSentiment.getSentimentScore().getNeutral();
				overallSentiment.setSentiment("NEUTRAL");
			}
			if (overallSentiment.getSentimentScore().getMixed() > highest) {
				highest = overallSentiment.getSentimentScore().getMixed();
				overallSentiment.setSentiment("MIXED");
			}
			if (overallSentiment.getSentimentScore().getNegative() > highest) {
				highest = overallSentiment.getSentimentScore().getNegative();
				overallSentiment.setSentiment("NEGATIVE");
			}

			
		} catch (ServiceException e) { throw e; } 
		catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception Occured in BO Layer!", ServiceConstants.E100);
		}
		
		LOG.info("Exit getOverallSentiment");
		return mapperUtil.populateOverallSentimentOutput(overallSentiment);
	}

}
