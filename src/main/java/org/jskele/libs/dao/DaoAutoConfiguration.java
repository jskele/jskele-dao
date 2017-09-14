package org.jskele.libs.dao;

import org.jskele.libs.dao.impl.DaoFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DaoAutoConfiguration {

	@Bean
	DaoFactory daoFactory(JdbcTemplate jdbcTemplate, ConversionService conversionService){
		return new DaoFactoryImpl(jdbcTemplate, conversionService);
	}

}
