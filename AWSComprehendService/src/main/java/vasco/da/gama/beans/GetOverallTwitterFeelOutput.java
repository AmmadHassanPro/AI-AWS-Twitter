package vasco.da.gama.beans;

import java.io.Serializable;

import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;

public class GetOverallTwitterFeelOutput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4430140588720558871L;
	
	private DetectSentimentResult overallSentiment;
	private DetectEntitiesResult overallEntities;
	private DetectKeyPhrasesResult overallKeyPhrases;
	
	public GetOverallTwitterFeelOutput(DetectSentimentResult sentiment, 
			DetectEntitiesResult entities, DetectKeyPhrasesResult keyPhrases) {
		setOverallSentiment(sentiment);
		setOverallEntities(entities);
		setOverallKeyPhrases(keyPhrases);
	}

	public DetectSentimentResult getOverallSentiment() {
		return overallSentiment;
	}

	public void setOverallSentiment(DetectSentimentResult overallSentiment) {
		this.overallSentiment = overallSentiment;
	}
	
	public DetectEntitiesResult getOverallEntities() {
		return overallEntities;
	}

	public void setOverallEntities(DetectEntitiesResult overallEntities) {
		this.overallEntities = overallEntities;
	}

	public DetectKeyPhrasesResult getOverallKeyPhrases() {
		return overallKeyPhrases;
	}

	public void setOverallKeyPhrases(DetectKeyPhrasesResult overallKeyPhrases) {
		this.overallKeyPhrases = overallKeyPhrases;
	}
	
	//TODO: update
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("Overall Sentiment: " + getOverallSentiment().getSentiment());
		out.append("\n");
		out.append("Positive Sentiment Rating: " + getOverallSentiment().getSentimentScore().getPositive());
		out.append("\n");
		out.append("Neutral Sentiment Rating: " + getOverallSentiment().getSentimentScore().getNeutral());
		out.append("\n");
		out.append("Mixed Sentiment Rating: " + getOverallSentiment().getSentimentScore().getMixed());
		out.append("\n");
		out.append("Negative Sentiment Rating: " + getOverallSentiment().getSentimentScore().getNegative());
		return out.toString();
	}
}
