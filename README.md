# AI-AWS-Twitter
Using Twitters API to get tweets with a banking # and feeding the tweets into Amazon Comprehend to perform the following:

1) Detect the named entities, we are able to find out who and what people are talking about, within the banking domain. 
2) Identify key phrases, the most discussed topics can be detected and topic modelling performed 
3) Sentiment analysis to gauge the mood of the content  After we identify the relevant topics, we will able to organize a knowledge base of tweets modeled around those topics.


Resources:
------------------------------------------------------------------------------------------------
Developer Guide: https://docs.aws.amazon.com/comprehend/latest/dg/comprehend-dg.pdf

Agorize Hacker Platform: https://globalai.agorize.com/en/challenges/openbankinghackathon2018/



As banking companies strive to get an accurate representation of the public opinion their products or services have in today’s highly competitive marketplace, traditional methods such as surveys just aren’t cutting it. A new approach to obtaining this information needs to be established. Rather than asking customers how they feel directly, scraping social media feeds for complaints and commendations may reveal their true sentiment.
By gauging customer expectations, companies can identify what aspect of their product or service needs to be improved. As part of the Open banking Hackathon 2018, team Vasco Da Gama would like to present their “Twitter Feed Analysis solution, using Amazon comprehend”.

[SHOW DIAGRAM]
Our solution uses the twitter API to pull Tweets having specific banking related keyword, which it then parses to Amazon Comprehend for analysis. Amazon Comprehend is a machine learning platform that can perform sentiment analysis and key phrase detection. Sentiment analysis gauges the mood of the topic while identifying key phrases reveals the most relevant and discussed topics, within the banking domain.
[Demo]
Let’s look first at how we analyze the sentiment. By passing a banking keywords, in this example we use [INSERT KEYWORD]. Our solution pulls Tweets containing this keyword (which we can see here*) and returns a negative and positive sentiment score which displays how confident Amazon comprehend is that the tweet is positive/negative.
**Companies will know that they are on the right track, when they start seeing an improved sentiment score for the opinions over time.

