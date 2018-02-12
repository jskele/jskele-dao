package org.jskele.libs.dao.impl2.params;

import java.lang.reflect.Method;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.impl2.DaoUtils;

public interface ParameterExtractor {

    static ParameterExtractor create(Method method, Class<? extends Dao> daoClass) {
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
