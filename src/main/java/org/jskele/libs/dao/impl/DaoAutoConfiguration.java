package org.jskele.libs.dao.impl;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.DbSchemaResolver;
import org.jskele.libs.dao.impl.mappers.LibDaoConversionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;

@Configuration
@ComponentScan
public class DaoAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ConversionService conversionService() {
        return new LibDaoConversionService();
    }

    @Bean
    @ConditionalOnMissingBean
    DbSchemaResolver dbSchemaResolver() {
        return new DaoAnnotationDbSchemaResolver();
    }

    private static class DaoAnnotationDbSchemaResolver implements DbSchemaResolver {
        @Override
        public String resolve(Class<?> daoClass) {
            Dao annotation = AnnotationUtils.findAnnotation(daoClass, Dao.class);
            return annotation.schema();
        }
    }
}
