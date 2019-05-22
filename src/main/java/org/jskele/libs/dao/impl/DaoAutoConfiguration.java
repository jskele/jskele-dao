package org.jskele.libs.dao.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@ComponentScan
public class DaoAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ConversionService conversionService() {
        return new DefaultConversionService();
    }

}
