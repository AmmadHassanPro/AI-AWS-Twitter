# AI-AWS-Twitter
Using Twitters Api to get the Hashtags and feeding the tweets into Amazon Comprehend to get valuable insights

Here are the steps for our POC:

1. Pull data in JSON format from Twitter/Facebook APIs (Need to create Develop account and get Auth keys) 
2. Covert JSON file to line delimited text file (We are processing using single file processing). 
3. Upload file to AWS S3 Bucket and specify output bucket (Must also specify IAM service role). 
4. Parse file to Amazon Comprehend  
5. Get Response and analyze results:
- Identify key noun phrases and their score confidence
- Get sentiment
- Get entities
