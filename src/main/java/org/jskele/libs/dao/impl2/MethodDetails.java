package org.jskele.libs.dao.impl2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.params.ParameterExtractor;

@RequiredArgsConstructor
public class MethodDetails {
    private final Method method;

    public Class<?> rowClass() {
        return DaoUtils.rowClass(method);
    }

    public boolean isQueryList() {
        return returnType().equals(List.class);
    }

    public boolean isUpdate() {
        return returnType().equals(int.class);
    }

    public boolean isBatchUpdate() {
        return returnType().equals(int[].class);
    }

    public ParameterExtractor parameterExtractor() {
        return ParameterExtractor.create(method);
    }

    private Class<?> returnType() {
        return method.getReturnType();
    }


    private boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        Annotation annotation = method.getAnnotation(annotationClass);
        return annotation != null;
    }
}
