package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class QueryListInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean accepts(Method method) {
        return false;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
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
