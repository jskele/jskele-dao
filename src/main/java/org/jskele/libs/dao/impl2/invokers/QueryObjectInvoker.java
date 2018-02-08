package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@RequiredArgsConstructor
class QueryObjectInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Method method;

    @Override
    public Object invoke(Object[] args) {
        return null;
    }
}
