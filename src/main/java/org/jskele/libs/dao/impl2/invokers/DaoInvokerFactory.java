package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.mappers.RowMapperFactory;
import org.jskele.libs.dao.impl2.params.ParamProviderFactory;
import org.jskele.libs.dao.impl2.sql.SqlFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlFactory sqlFactory;
    private final ParamProviderFactory paramProviderFactory;
    private final RowMapperFactory rowMapperFactory;

    public DaoInvoker create(Method method) {
        if (isQueryList(method)) {
            return new QueryListInvoker(
                jdbcTemplate,
                sqlFactory.createSql(method),
                paramProviderFactory.create(method),
                rowMapperFactory.create(method)
            );
        }

        if (isQueryObject(method)) {
            return new QueryObjectInvoker(
                jdbcTemplate,
                sqlFactory.createSql(method),
                paramProviderFactory.create(method),
                rowMapperFactory.create(method)
            );
        }

        if (isUpdateSingle(method)) {
            return new UpdateSingleInvoker(
                jdbcTemplate,
                sqlFactory.createSql(method),
                paramProviderFactory.create(method)
            );
        }

        if (isUpdateBatch(method)) {
            return new UpdateBatchInvoker(
                jdbcTemplate,
                sqlFactory.createSql(method),
                paramProviderFactory.create(method)
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
