package org.jskele.libs.dao.impl2.params2;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

@RequiredArgsConstructor
class ArgumentsParameterExtractor implements ParameterExtractor {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final String[] parameterNames;

    static ArgumentsParameterExtractor create(Method method) {
        String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        return new ArgumentsParameterExtractor(parameterNames);
    }

    @Override
    public String[] names() {
        return parameterNames;
    }

    @Override
    public Object[] values(Object[] args) {
        return args;
    }
}
