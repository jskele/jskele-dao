package org.jskele.dao.impl.params;

import org.jskele.dao.impl.DaoUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

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

    default Class<?> getTypeOf(String paramName) {
        int idIndex = Arrays.asList(names()).indexOf(paramName);
        if (idIndex == -1) {
            return null;
        }
        return types()[idIndex];
    }
}
