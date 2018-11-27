package vasco.da.gama.utils;

import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import vasco.da.gama.beans.GetOverallTwitterSentimentOutput;

@Component
public class ServiceMapperUtil {
	public GetOverallTwitterSentimentOutput populateOverallSentimentOutput(DetectSentimentResult overallSentiment) {
		return new GetOverallTwitterSentimentOutput(overallSentiment);
	}
}
