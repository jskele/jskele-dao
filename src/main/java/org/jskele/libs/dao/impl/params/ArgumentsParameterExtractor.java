package org.jskele.libs.dao.impl.params;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

@RequiredArgsConstructor
class ArgumentsParameterExtractor implements ParameterExtractor {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final String[] names;
    private final Class<?>[] types;

    static ArgumentsParameterExtractor create(Method method) {
        String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        return new ArgumentsParameterExtractor(parameterNames, method.getParameterTypes());
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
        return args;
    }
}
