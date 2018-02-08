package org.jskele.libs.dao.impl2;

import static com.google.common.base.Preconditions.checkArgument;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.core.ResolvableType;

public class DaoUtils {
    public static boolean isBean(Class<?> paramType) {
        return constructorProperties(paramType) != null;
    }

    public static String[] constructorProperties(Class<?> paramType) {
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

    public static Class<?> rowClass(Method method) {
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);

        if (resolvableType.hasGenerics()) {
            ResolvableType[] generics = resolvableType.getGenerics();
            checkArgument(generics.length == 1);
            return generics[0].resolve();
        }

        return resolvableType.resolve();
    }
}
