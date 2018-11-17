package com.the.scott.one;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.the.scott.one.external.TwitterEO;
import com.the.scott.one.utils.ServiceAuthUtil;
import com.the.scott.one.utils.ServiceConstants;
import com.the.scott.one.utils.ServiceUtil;

import java.util.Map;


@SpringBootApplication
public class AwsComprehendServiceApplication {
	
	@Autowired
	static
	ServiceUtil util;
	
	@Autowired
	ServiceAuthUtil authUtil;
	
	@Autowired
	static
	TwitterEO twitterEO;

	public static void main(String[] args) {
		//SpringApplication.run(AwsComprehendServiceApplication.class, args);
		Map<String, String> responseMap = null;
		
		try {
			responseMap = ServiceAuthUtil.getTwitterAuthToken();
			
			if (responseMap.get(ServiceConstants.RESPONSE_STATUS).equals(ServiceConstants.STATUS_OK)) {
				String authToken = responseMap.get(ServiceConstants.RESPONSE_MESSAGE);
				String requestUserAccess = "https://api.twitter.com/oauth/authorize?oauth_token=" + authToken;
				
				
				
		        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/main/resources/chromedriver.exe");
		        
		        WebDriver chromeDriver = new ChromeDriver();
		        WebDriverWait wait = new WebDriverWait(chromeDriver, 100);

		        // And now use this to visit Google
		        chromeDriver.get(requestUserAccess);

		        String previousURL = chromeDriver.getCurrentUrl();
	        	ExpectedCondition<Boolean> e = new ExpectedCondition<Boolean>() {
	        		@Override
	                public Boolean apply(WebDriver d) {
	                  return (!(d.getCurrentUrl().equals(previousURL)));
	                }
	             };
		       
	             wait.until(e);
	             String curUrl = chromeDriver.getCurrentUrl();
	             System.out.println("CurURL: " + curUrl);
	             WebElement codeElement = chromeDriver.findElement(By.tagName("CODE"));
	             String code = codeElement.getText();
	             System.out.println("code: " + code);
	             chromeDriver.close();
	             
	             responseMap.clear();
	             responseMap = ServiceAuthUtil.getTwitterAccessToken(authToken, code);
	             String accessToken = responseMap.get(ServiceConstants.RESPONSE_MESSAGE);
	             String[] stuff = accessToken.split(",");
	             System.out.println("stuff[0]: " + stuff[0] + " stuff[1]: " + stuff[1]);
	             try {
	            	 responseMap = TwitterEO.searchTwitterForString("#Discover", stuff[0], stuff[1]);
	             } catch (Exception exception) {
	            	 System.out.println("responseMap exception: " + exception.getMessage());
	             }
	             System.out.println("responseMap: " + responseMap.get(ServiceConstants.RESPONSE_MESSAGE));
	             
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}
}
