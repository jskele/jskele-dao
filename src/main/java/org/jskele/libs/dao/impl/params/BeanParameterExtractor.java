package org.jskele.libs.dao.impl.params;

import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.DaoUtils;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
class BeanParameterExtractor implements ParameterExtractor {
    private final String[] names;
    private final Method[] readMethods;
    private final Class<?>[] types;

    static BeanParameterExtractor create(Class<?> beanClass) {
        String[] names = DaoUtils.beanProperties(beanClass);
        Method[] readMethods = readMethods(beanClass, names);

        Class<?>[] types = Arrays.stream(readMethods)
                .map(Method::getReturnType)
                .toArray(Class<?>[]::new);

        return new BeanParameterExtractor(names, readMethods, types);
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

    @Override
    public String[] names() {
        return names;
    }

    @Override
    public Class<?>[] types() {
        return types;
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
}
