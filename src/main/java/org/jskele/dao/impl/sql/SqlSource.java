package org.jskele.dao.impl.sql;

import org.jskele.dao.DbSchemaResolver;
import org.jskele.dao.GenerateSql;
import org.jskele.dao.impl.params.ParameterExtractor;

import java.lang.reflect.Method;

import static org.jskele.dao.impl.DaoUtils.hasAnnotation;

public interface SqlSource {

    static SqlSource create(Class<?> daoClass, Method method, ParameterExtractor extractor, boolean isBatchInsertOrUpdate, DbSchemaResolver dbSchemaResolver) {
        if (hasAnnotation(method, GenerateSql.class)) {
            return new SqlGenerator(daoClass, method, extractor, dbSchemaResolver).createSource(isBatchInsertOrUpdate);
        }

        return new ClasspathSqlLoader(method, extractor).createSource();
    }

    String generateSql(Object[] args);
}
