package org.jskele.libs.dao.impl2.invokers;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class QueryListInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String sql;
    private final ParamProvider paramProvider;
    private final RowMapper<?> rowMapper;

    public Object invoke(Object[] args) {
        SqlParameterSource parameterSource = paramProvider.getParams(args);

        return jdbcTemplate.query(sql, parameterSource, rowMapper);
    }

}
