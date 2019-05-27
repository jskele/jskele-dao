package org.jskele.libs.dao.impl;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor
public class MethodDetails {
    private final Method method;

    public boolean isQueryList() {
        return hasReturnType(List.class);
    }

    public boolean isInsertUpdateOrDelete() {
        return hasReturnType(int.class) || hasReturnType(void.class);
    }

    public boolean isBatchInsertOrUpdate() {
        return hasReturnType(int[].class);
    }

    private boolean hasReturnType(Class<?> returnType) {
        return DaoUtils.hasReturnType(method, returnType);
    }

}
