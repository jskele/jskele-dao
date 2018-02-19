package org.jskele.libs.dao.impl;

import java.lang.reflect.Method;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MethodDetails {
    private final Method method;

    public boolean isQueryList() {
        return hasReturnType(List.class);
    }

    public boolean isUpdate() {
        return hasReturnType(int.class);
    }

    public boolean isBatchUpdate() {
        return hasReturnType(int[].class);
    }

    private boolean hasReturnType(Class<?> returnType) {
        return DaoUtils.hasReturnType(method, returnType);
    }

}
