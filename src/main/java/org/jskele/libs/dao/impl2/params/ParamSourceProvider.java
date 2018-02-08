package org.jskele.libs.dao.impl2.params;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public abstract class ParamSourceProvider {

    ParamSourceProvider create(DataSource dataSource, Method method) {
        int parameterCount = method.getParameterCount();

        if (parameterCount == 0) {
            return new EmptyParamProvider();
        }

        if (parameterCount == 1 && isBean(method.getParameterTypes()[0])) {
            return BeanParamProvider.create0(dataSource, method);
        }

        return ArgumentsParamProvider.create0(dataSource, method);
    }

    boolean isBean(Class<?> paramType){
        Constructor<?>[] constructors = paramType.getConstructors();

        boolean found = false;

        for (Constructor<?> constructor : constructors) {
            ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
            if (annotation == null) {
                continue;
            }

            if (found) {
                return false;
            }

            found = true;
        }

        return found;
    }

    static String[] constructorProperties(Class<?> paramType){
        Constructor<?>[] constructors = paramType.getConstructors();

        String[] found = null;

        for (Constructor<?> constructor : constructors) {
            ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
            if (annotation == null) {
                continue;
            }

            if (found != null) {
                return null;
            }

            found = annotation.value();
        }

        return found;
    }

    public abstract SqlParameterSource getParams(Object[] args);
}
