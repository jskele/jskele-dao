package org.jskele.libs.dao.impl.sql;

import static org.jskele.libs.dao.impl.DaoUtils.hasAnnotation;

import java.lang.reflect.Method;
import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl.params.ParameterExtractor;

public interface SqlSource {

	static SqlSource create(Class<?> daoClass, Method method,
			ParameterExtractor extractor, boolean isBatchInsertOrUpdate) {
		if (hasAnnotation(method, GenerateSql.class)) {
			return new SqlGenerator(daoClass, method, extractor)
					.createSource(isBatchInsertOrUpdate);
		}

		return new ClasspathSqlLoader(method, extractor).createSource();
	}

	String generateSql(Object[] args);

}
