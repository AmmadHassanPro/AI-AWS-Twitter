package vasco.da.gama.utils;

import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import vasco.da.gama.beans.GetOverallTwitterFeelOutput;

@Component
public class ServiceMapperUtil {
	public GetOverallTwitterFeelOutput populateOverallSentimentOutput(DetectSentimentResult overallSentiment,
			DetectEntitiesResult overallEntities, DetectKeyPhrasesResult overallKeyPhrases) {
		return new GetOverallTwitterFeelOutput(overallSentiment, overallEntities, overallKeyPhrases);
	}
}
