package mdev.OrderProcessingSpring;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author markodevelopment (Mihálovics Márkó)
 * @// TODO: 2/5/20 Create the Spring version of OrderProcessing..
 */
@SpringBootApplication
public class OPSpringApp {

	public static final Logger log = (Logger) LoggerFactory.getLogger(OPSpringApp.class);

	public static void main(String[] args) {
		SpringApplication.run(OPSpringApp.class, args);
	}

}