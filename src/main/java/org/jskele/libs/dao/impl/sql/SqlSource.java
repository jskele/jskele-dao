package org.jskele.libs.dao.impl.sql;

import static org.jskele.libs.dao.impl.DaoUtils.hasAnnotation;

import java.lang.reflect.Method;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl.params.ParameterExtractor;

public interface SqlSource {

    static SqlSource create(Class<? extends Dao> daoClass, Method method, ParameterExtractor extractor) {
        if (hasAnnotation(method, GenerateSql.class)) {
            return new SqlGenerator(daoClass, method, extractor).createSource();
        }

        return new ClasspathSqlLoader(method, extractor).createSource();
    }

    String generateSql(Object[] args);
}
