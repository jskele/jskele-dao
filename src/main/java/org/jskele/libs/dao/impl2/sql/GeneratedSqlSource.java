package org.jskele.libs.dao.impl2.sql;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.GenerateSql;

@RequiredArgsConstructor
class GeneratedSqlSource {
    private final Method method;

    public boolean isPresent(){
        GenerateSql annotation = method.getAnnotation(GenerateSql.class);
        return annotation != null;
    }

    public String getSql() {
        return null;
    }
}
