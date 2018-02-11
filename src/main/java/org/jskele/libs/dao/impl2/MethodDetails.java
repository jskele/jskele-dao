package org.jskele.libs.dao.impl2;

import java.lang.reflect.Method;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.jskele.libs.dao.impl2.params2.ParameterExtractor;

@RequiredArgsConstructor
public class MethodDetails {
    private final Method method;

    public Class<?> rowClass() {
        return DaoUtils.rowClass(method);
    }

    public boolean isQueryList(){
        boolean isList = method.getReturnType().isAssignableFrom(List.class);

        return isList;
    }

    public ParameterExtractor paramProvider(){

    }
}
