package org.jskele.libs.dao.impl2.params2;

import java.lang.reflect.Method;

import org.jskele.libs.dao.impl2.DaoUtils;

public interface ParameterExtractor {

    String[] names();

    Class<?>[] types();

    Object[] values(Object[] args);

    static ParameterExtractor create(Method method) {
        int parameterCount = method.getParameterCount();
        if (parameterCount == 1 && DaoUtils.isBean(method.getParameterTypes()[0])) {
            return BeanParameterExtractor.create(method);
        }

        return ArgumentsParameterExtractor.create(method);
    }
}
