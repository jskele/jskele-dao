package org.jskele.libs.dao.impl2.sql;

import java.lang.reflect.Method;

import org.jskele.libs.dao.impl2.params.ParamProvider;

public class SqlProviderFactory {

    public SqlProvider createSql(Method method, ParamProvider paramProvider) {
        if (isGenerate(method)) {
            return new GeneratedSqlSource(method, paramProvider);
        }

        ClasspathSqlProvider classpathSqlProvider = ClasspathSqlProvider.create(method);
        if (classpathSqlProvider.isPresent()) {
            return classpathSqlProvider;
        }

        throw new IllegalStateException("No SQL supplier found");
    }

    private boolean isClasspath(Method method) {
        return false;
    }

    private boolean isGenerate(Method method) {
        return false;
    }
}
