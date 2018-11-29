package vasco.da.gama.controller;

import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import vasco.da.gama.beans.GetOverallTwitterFeelOutput;
import vasco.da.gama.bo.ServiceBO;
import vasco.da.gama.exceptions.ServiceException;
import vasco.da.gama.external.ComprehendEO;
import vasco.da.gama.utils.ServiceConstants;

@RestController
public class ServiceController {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);
	
	@Autowired
	ServiceBO serviceBO;
	
	@Autowired
	ComprehendEO comprehendEO;
	
	@GetMapping("/getOverallTwitterFeel")
	public GetOverallTwitterFeelOutput getOverallTwitterFeel(@RequestParam String searchString) throws ServiceException {
		LOG.info("Enter getOverallTwitterFeel");
		GetOverallTwitterFeelOutput ret = null;
		try {
			String encodedInput = URLEncoder.encode(searchString, "UTF-8");
			LOG.info("Encoded Input: {}", encodedInput);
			ret = serviceBO.getOverallSentiment(encodedInput);
		} catch (ServiceException e) { throw e; }
		catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception occured in controller!", ServiceConstants.E100);
		}
		LOG.info("Exit getOverallTwitterFeel");
		return ret;
	}
	
	@GetMapping("/getSingleSentiment")
	public DetectSentimentResult getInputSentiment(@RequestParam String searchString) {
		try {
			return comprehendEO.getSingleSentiment(searchString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
