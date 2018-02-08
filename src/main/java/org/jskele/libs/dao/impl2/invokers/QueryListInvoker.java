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
    private final String sql;
    private final RowMapper<?> rowMapper;

    public static QueryListInvoker create(
        NamedParameterJdbcTemplate jdbcTemplate,
        Method method
    ) {
        return new QueryListInvoker(
            jdbcTemplate,
            method,
            sql(method),
            rowMapper(method)
        );
    }

    private static String sql(Method method) {
        return null;
    }

    private static RowMapper rowMapper(Method method) {
        return null;
    }

    public Object invoke(Object[] args) {
        SqlParameterSource parameterSource = parameterSource(method, args);

        return jdbcTemplate.query(sql, parameterSource, rowMapper);
    }

    private SqlParameterSource parameterSource(Method method, Object[] args) {
        return null;
    }

}
