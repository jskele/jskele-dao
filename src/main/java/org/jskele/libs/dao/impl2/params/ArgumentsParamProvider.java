package org.jskele.libs.dao.impl2.params;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl.DaoSqlParameterSource;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class ArgumentsParamProvider extends ParamProvider {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final DataSource dataSource;
    private final String[] parameterNames;

    static ArgumentsParamProvider create0(DataSource dataSource, Method method) {
        String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);

        return new ArgumentsParamProvider(dataSource, parameterNames);
    }

    @Override
    public SqlParameterSource getParams(Object[] args) {
        DaoSqlParameterSource daoSqlParameterSource = new DaoSqlParameterSource(dataSource);

        for (int i = 0; i < args.length; i++) {
            String name = parameterNames[i];
            Object value = args[i];

            daoSqlParameterSource.addValue(name, value);
        }

        return daoSqlParameterSource;
    }
}
