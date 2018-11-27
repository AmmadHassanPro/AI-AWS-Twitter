package com.the.scott.one.utils;

import org.springframework.stereotype.Component;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import the.scott.one.beans.GetOverallTwitterSentimentOutput;

@Component
public class ServiceMapperUtil {
	public GetOverallTwitterSentimentOutput populateOverallSentimentOutput(DetectSentimentResult overallSentiment) {
		return new GetOverallTwitterSentimentOutput(overallSentiment);
	}
}
