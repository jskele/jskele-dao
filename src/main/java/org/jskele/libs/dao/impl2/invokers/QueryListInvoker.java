package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class QueryListInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Method method;

    public Object invoke(Object[] args) {
        String sql = sql(method);
        SqlParameterSource parameterSource = parameterSource(method, args);
        RowMapper rowMapper = rowMapper(method);

        return jdbcTemplate.query(sql, parameterSource, rowMapper);
    }

    private String sql(Method method) {
        return null;
    }

    private RowMapper rowMapper(Method method) {
        return null;
    }

    private SqlParameterSource parameterSource(Method method, Object[] args) {
        return null;
    }


}
