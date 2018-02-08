package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@FunctionalInterface
public interface DaoInvokerConstructor {

    DaoInvoker create(NamedParameterJdbcTemplate jdbcTemplate, Method method);

}
