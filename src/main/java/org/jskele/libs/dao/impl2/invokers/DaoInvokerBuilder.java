package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl.ConstructorRowMapper;
import org.jskele.libs.dao.impl.ConvertingSingleColumnRowMapper;
import org.jskele.libs.dao.impl2.DaoUtils;
import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.jskele.libs.dao.impl2.sql.SqlProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@RequiredArgsConstructor
class DaoInvokerBuilder {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ConversionService conversionService;
    private final Method method;

    private Class<?> rowClass;

    DaoInvoker build(){

        SqlProvider sqlProvider;
        ParamProvider paramProvider;
        RowMapper<?> rowMapper = rowMapper();

        return args -> {
            String sql = sqlProvider.getSql(args);
            SqlParameterSource params = paramProvider.getParams(args);
            return jdbcTemplate.query(sql, params, rowMapper);
        };
    }

    private RowMapper<?> rowMapper() {
        Class<?> rowClass = DaoUtils.rowClass(method);

        if (DaoUtils.isBean(rowClass)) {
            return new ConstructorRowMapper<>(rowClass, conversionService);
        }

        return new ConvertingSingleColumnRowMapper<>(rowClass, conversionService);
    }
}
