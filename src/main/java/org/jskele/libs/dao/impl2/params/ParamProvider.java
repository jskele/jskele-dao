package org.jskele.libs.dao.impl2.params;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.jskele.libs.dao.impl2.DaoUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public abstract class ParamProvider {

    static ParamProvider create(DataSource dataSource, Method method) {
        int parameterCount = method.getParameterCount();

        if (parameterCount == 0) {
            return new EmptyParamProvider();
        }

        if (parameterCount == 1 && DaoUtils.isBean(method.getParameterTypes()[0])) {
            return BeanParamProvider.create0(dataSource, method);
        }

        return ArgumentsParamProvider.create0(dataSource, method);
    }

    public abstract SqlParameterSource getParams(Object[] args);

    public abstract String[] getNames();
}
