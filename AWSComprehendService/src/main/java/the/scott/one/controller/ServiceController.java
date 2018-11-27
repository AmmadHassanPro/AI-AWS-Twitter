package the.scott.one.controller;

import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.the.scott.one.exceptions.ServiceException;
import com.the.scott.one.utils.ServiceConstants;

import the.scott.one.beans.GetOverallTwitterSentimentOutput;
import the.scott.one.bo.ServiceBO;

@Controller
public class ServiceController {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);
	
	@Autowired
	ServiceBO serviceBO;
	
	@GetMapping("/getsentiment")
	public GetOverallTwitterSentimentOutput getOverallTwitterSentiment(@RequestParam String id) throws ServiceException {
		LOG.info("Enter getOverallTwitterSentiment");
		try {
			String encodedInput = URLEncoder.encode(id, "UTF-8");
			LOG.info("Encoded Input: {}", encodedInput);
			return serviceBO.getOverallSentiment(encodedInput);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception occured in controller!", ServiceConstants.E100);
		}
	}
}
