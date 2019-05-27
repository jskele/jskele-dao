package org.jskele.libs.dao.impl.params;

import org.jskele.libs.dao.impl.DaoUtils;

import java.lang.reflect.Method;

public interface ParameterExtractor {

	static ParameterExtractor create(Method method, Class<?> daoClass) {
		Class<?> beanClass = DaoUtils.beanClass(method, daoClass);
		if (beanClass != null) {
			return BeanParameterExtractor.create(beanClass);
		}

		return ArgumentsParameterExtractor.create(method);
	}

	String[] names();

	Class<?>[] types();

	Object[] values(Object[] args);

}
