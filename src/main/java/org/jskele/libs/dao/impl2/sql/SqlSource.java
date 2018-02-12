package org.jskele.libs.dao.impl2.sql;

import static org.jskele.libs.dao.impl2.DaoUtils.hasAnnotation;

import java.lang.reflect.Method;

import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl2.params.ParameterExtractor;

public interface SqlSource {

    static SqlSource create(Method method, ParameterExtractor extractor) {
        if (hasAnnotation(method, GenerateSql.class)) {
            return new SqlGenerator(method, extractor).createSource();
        }

        return new ClasspathSqlLoader(method, extractor).createSource();
    }

    String generateSql(Object[] args);
}
