package org.jskele.libs.dao.impl2.params2;

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

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.DaoUtils;

@RequiredArgsConstructor
class BeanParameterExtractor implements ParameterExtractor {
    private final String[] parameterNames;
    private final Method[] readMethods;

    static BeanParameterExtractor create(Method method) {
        Class<?> beanClass = method.getParameterTypes()[0];
        String[] names = DaoUtils.constructorProperties(beanClass);
        Method[] readMethods = readMethods(beanClass, names);
        return new BeanParameterExtractor(names, readMethods);
    }

    @Override
    public String[] names() {
        return parameterNames;
    }

    @Override
    public Object[] values(Object[] args) {
        Object bean = args[0];
        return Arrays.stream(readMethods)
            .map(a -> readValue(bean, a))
            .toArray();
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

            return Arrays.stream(properties)
                .map(collect::get)
                .toArray(Method[]::new);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
