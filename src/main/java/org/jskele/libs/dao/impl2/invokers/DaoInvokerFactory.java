package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DaoInvoker createInvoker(Method method) {
        DaoInvokerConstructor constructor = constructor(method);

        return constructor.create(jdbcTemplate, method);
    }


    private DaoInvokerConstructor constructor(Method method) {
        if (isQueryList(method)) {
            return QueryListInvoker::create;
        }

        if (isQueryObject(method)) {
            return QueryObjectInvoker::new;
        }

        if (isUpdateSingle(method)) {
            return UpdateSingleInvoker::new;
        }

        if (isUpdateBatch(method)) {
            return UpdateBatchInvoker::new;
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
