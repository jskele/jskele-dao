package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.MethodDetails;
import org.jskele.libs.dao.impl2.mappers.RowMapperFactory;
import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.jskele.libs.dao.impl2.params.ParamProviderFactory;
import org.jskele.libs.dao.impl2.sql.SqlProvider;
import org.jskele.libs.dao.impl2.sql.SqlProviderFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlProviderFactory sqlProviderFactory;
    private final ParamProviderFactory paramProviderFactory;
    private final RowMapperFactory rowMapperFactory;

    public DaoInvoker create(Method method) {
        MethodDetails details = new MethodDetails(method);

        ParamProvider paramProvider = paramProviderFactory.create(method);
        SqlProvider sqlProvider = sqlProviderFactory.createSql(method, paramProvider);
        RowMapper<?> rowMapper = rowMapperFactory.create(method);

        if (isQueryList(method)) {
            return args -> {
                SqlParameterSource params = paramProvider.getParams(args);
                String sql = sqlProvider.createSql(params);
                return jdbcTemplate.query(sql, params, rowMapper);
            };
        }

        if (isQueryObject(method)) {
            return args -> {
                SqlParameterSource params = paramProvider.getParams(args);
                String sql = sqlProvider.createSql(params);
                return jdbcTemplate.queryForObject(sql, params, rowMapper);
            };
        }

        if (isUpdateSingle(method)) {
            return new UpdateSingleInvoker(
                jdbcTemplate,
                sql,
                paramProvider
            );
        }

        if (isUpdateBatch(method)) {
            return new UpdateBatchInvoker(
                jdbcTemplate,
                sql,
                paramProvider
            );
        }

        throw new IllegalStateException("DaoInvoker not found for Method " + method);
    }

    private boolean isUpdateBatch(Method method) {
        return false;
    }

    private boolean isUpdateSingle(Method method) {
        return false;
    }

    private boolean isQueryObject(Method method) {
        return false;
    }

    private boolean isQueryList(Method method) {
        return false;
    }
}
