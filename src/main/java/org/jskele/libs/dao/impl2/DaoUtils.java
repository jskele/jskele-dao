package org.jskele.libs.dao.impl2;

import static com.google.common.base.Preconditions.checkArgument;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public class DaoUtils {
    public static boolean isBean(Class<?> paramType) {
        return beanProperties(paramType) != null;
    }

    public static String[] beanProperties(Class<?> paramType) {
        Constructor<?> constructor = beanConstructor(paramType);
        if (constructor == null) {
            return null;
        }

        return constructor.getAnnotation(ConstructorProperties.class).value();
    }

    public static Constructor<?> beanConstructor(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getConstructors();

        Constructor<?> found = null;

        for (Constructor<?> constructor : constructors) {
            ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
            if (annotation == null) {
                continue;
            }

            if (found != null) {
                return null;
            }

            found = constructor;
        }

        return found;
    }

    public static Class<?> rowClass(Method method) {
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);

        if (resolvableType.hasGenerics()) {
            ResolvableType[] generics = resolvableType.getGenerics();
            checkArgument(generics.length == 1);
            return generics[0].resolve();
        }

        return resolvableType.resolve();
    }

    public static boolean hasAnnotation(Method method, Class<? extends Annotation> annotationClass) {
        return method.getAnnotation(annotationClass) != null;
    }

    public static boolean hasReturnType(Method method, Class<?> returnType) {
        return method.getReturnType().equals(returnType);
    }
}
