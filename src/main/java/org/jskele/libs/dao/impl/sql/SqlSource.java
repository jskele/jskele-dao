package org.jskele.libs.dao.impl.sql;

import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl.params.ParameterExtractor;

import java.lang.reflect.Method;

import static org.jskele.libs.dao.impl.DaoUtils.hasAnnotation;

public interface SqlSource {

    static SqlSource create(Class<?> daoClass, Method method, ParameterExtractor extractor) {
        if (hasAnnotation(method, GenerateSql.class)) {
            return new SqlGenerator(daoClass, method, extractor).createSource();
        }

        return new ClasspathSqlLoader(method, extractor).createSource();
    }

    String generateSql(Object[] args);
}
