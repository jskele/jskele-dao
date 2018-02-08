package org.jskele.libs.dao.impl2.invokers;

import java.util.Collection;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class UpdateBatchInvoker implements DaoInvoker {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String sql;
    private final ParamProvider paramProvider;

    @Override
    public Object invoke(Object[] args) {

        Collection<?> collection = (Collection<?>) args[0];

        SqlParameterSource[] parameterSources = (SqlParameterSource[]) collection.stream()
            .map(arg -> paramProvider.getParams(new Object[] { arg }))
            .toArray();

        return jdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
