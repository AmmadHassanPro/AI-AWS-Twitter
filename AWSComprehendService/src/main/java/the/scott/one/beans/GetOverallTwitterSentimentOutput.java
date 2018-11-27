package the.scott.one.beans;

import java.io.Serializable;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

public class GetOverallTwitterSentimentOutput implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4430140588720558871L;
	
	private DetectSentimentResult overallSentiment;
	
	public GetOverallTwitterSentimentOutput(DetectSentimentResult sentiment) {
		setOverallSentiment(sentiment);
	}

	public DetectSentimentResult getOverallSentiment() {
		return overallSentiment;
	}

	public void setOverallSentiment(DetectSentimentResult overallSentiment) {
		this.overallSentiment = overallSentiment;
	}
	
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
