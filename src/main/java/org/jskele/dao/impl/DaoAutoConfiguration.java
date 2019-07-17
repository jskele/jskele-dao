package org.jskele.dao.impl;

import org.jskele.dao.DbSchemaResolver;
import org.jskele.dao.Dao;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

@Configuration
@ComponentScan
public class DaoAutoConfiguration {

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
