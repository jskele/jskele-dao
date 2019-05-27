package org.jskele.libs;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@SpringBootApplication
public class Application {

	@Bean
	ConversionService conversionService() {
		return new DefaultConversionService();
	}

}
