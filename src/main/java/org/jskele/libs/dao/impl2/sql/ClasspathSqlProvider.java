package org.jskele.libs.dao.impl2.sql;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

import com.google.common.io.Resources;
import com.google.common.reflect.Reflection;

@RequiredArgsConstructor
class ClasspathSqlProvider {

    private final URL resourceUrl;

    static ClasspathSqlProvider create(Method method){
        return new ClasspathSqlProvider(resourceUrl(method));
    }

    public boolean isPresent(){
        return resourceUrl != null;
    }

    public String getSql() {
        try {
            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL resourceUrl(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();

        String methodName = method.getName();
        String packageName = Reflection.getPackageName(declaringClass);
        String fileName = "/" + packageName.replace(".", "/") + "/" + methodName + ".sql";

        return declaringClass.getResource(fileName);
    }

}
