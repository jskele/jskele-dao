package org.jskele.libs.dao.impl2.invokers;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class UpdateSingleInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String sql;
    private final ParamProvider paramProvider;

    @Override
    public Object invoke(Object[] args) {
        SqlParameterSource params = paramProvider.getParams(args);

        return jdbcTemplate.update(sql, params);
    }
}
