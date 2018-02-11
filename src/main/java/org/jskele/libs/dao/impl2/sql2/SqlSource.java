package org.jskele.libs.dao.impl2.sql2;

import java.lang.reflect.Method;

import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl2.params2.ParameterExtractor;

public interface SqlSource {

    static SqlSource create(Method method, ParameterExtractor extractor) {
        if (method.getAnnotation(GenerateSql.class) != null) {
            return new SqlGenerator(method,extractor).createSource();
        }

        return new ClasspathSqlLoader(method, extractor).createSource();
    }

    String generateSql(Object[] args);
}
