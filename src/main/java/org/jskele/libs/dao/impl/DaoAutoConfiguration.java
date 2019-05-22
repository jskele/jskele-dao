package org.jskele.libs.dao.impl;

import org.jskele.libs.dao.impl.mappers.LibDaoConversionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

@Configuration
@ComponentScan
public class DaoAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ConversionService conversionService() {
        return new LibDaoConversionService();
    }

}
