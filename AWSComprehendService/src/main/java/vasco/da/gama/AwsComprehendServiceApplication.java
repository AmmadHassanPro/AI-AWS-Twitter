package vasco.da.gama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AwsComprehendServiceApplication {	

	public static void main(String[] args) {		
		SpringApplication.run(AwsComprehendServiceApplication.class, args);
	}
}
