package the.scott.one.beans;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

public class SentimentBean {
	
	private DetectSentimentResult sentiment;
	private String tweet;
	
	public SentimentBean(String tweet, DetectSentimentResult sentiment ) {
		setTweet(tweet);
		setSentiment(sentiment);
	}
	
	public DetectSentimentResult getSentiment() {
		return sentiment;
	}
	public void setSentiment(DetectSentimentResult sentiment) {
		this.sentiment = sentiment;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("Tweet Text: " + getTweet());
		out.append("\n");
		out.append("Overall Sentiment: " + getSentiment().getSentiment());
		out.append("\n");
		out.append("Positive Sentiment Rating: " + getSentiment().getSentimentScore().getPositive());
		out.append("\n");
		out.append("Neutral Sentiment Rating: " + getSentiment().getSentimentScore().getNeutral());
		out.append("\n");
		out.append("Mixed Sentiment Rating: " + getSentiment().getSentimentScore().getMixed());
		out.append("\n");
		out.append("Negative Sentiment Rating: " + getSentiment().getSentimentScore().getNegative());
		return out.toString();
	}
}
