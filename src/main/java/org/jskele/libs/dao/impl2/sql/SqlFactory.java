package org.jskele.libs.dao.impl2.sql;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.jskele.libs.dao.impl2.params.ParamProvider;

public class SqlFactory {

    public String createSql(Method method, ParamProvider paramProvider) {
        Supplier<String> sqlSupplier = SqlSupplier(method);

        return sqlSupplier.get();
    }

    private Supplier<String> SqlSupplier(Method method) {
        if (isGenerate(method)) {
            return new GeneratedSqlSource(method)::getSql;
        }

        ClasspathSqlSource classpathSqlSource = ClasspathSqlSource.create(method);
        if (classpathSqlSource.isPresent()) {
            return classpathSqlSource::getSql;
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
