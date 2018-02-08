package org.jskele.libs.dao.impl2.params;

import static java.util.stream.Collectors.toMap;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl.DaoSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class BeanParamProvider extends ParamSourceProvider {
    private final DataSource dataSource;
    private final String[] parameterNames;
    private final Method[] readMethods;


    static BeanParamProvider create0(DataSource dataSource, Method method) {
        Class<?> beanClass = method.getParameterTypes()[0];
        String[] names = constructorProperties(beanClass);
        Method[] readMethods = readMethods(beanClass, names);

        return new BeanParamProvider(dataSource, names, readMethods);
    }

    @Override
    public SqlParameterSource getParams(Object[] args) {
        DaoSqlParameterSource parameterSource = new DaoSqlParameterSource(dataSource);

        Object bean = args[0];
        for (int i = 0; i < parameterNames.length; i++) {
            String name = parameterNames[i];
            Object value = readValue(bean, readMethods[i]);

            parameterSource.addValue(name, value);
        }

        return parameterSource;

    }

    private Object readValue(Object bean, Method readMethod) {
        try {
            return readMethod.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method[] readMethods(Class<?> beanClass, String[] properties) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

            Map<String, Method> collect = Arrays.stream(beanInfo.getPropertyDescriptors())
                .collect(toMap(
                    FeatureDescriptor::getName,
                    PropertyDescriptor::getReadMethod
                ));

            return (Method[]) Arrays.stream(properties)
                .map(collect::get)
                .toArray();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
