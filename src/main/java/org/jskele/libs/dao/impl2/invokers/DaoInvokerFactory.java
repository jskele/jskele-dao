package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl.ConstructorRowMapper;
import org.jskele.libs.dao.impl.ConvertingSingleColumnRowMapper;
import org.jskele.libs.dao.impl.DaoSqlParameterSource;
import org.jskele.libs.dao.impl2.DaoUtils;
import org.jskele.libs.dao.impl2.MethodDetails;
import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.jskele.libs.dao.impl2.params.ParamProviderFactory;
import org.jskele.libs.dao.impl2.params2.ParameterExtractor;
import org.jskele.libs.dao.impl2.sql.SqlProvider;
import org.jskele.libs.dao.impl2.sql.SqlProviderFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlProviderFactory sqlProviderFactory;
    private final ParamProviderFactory paramProviderFactory;
    private final DataSource dataSource;
    private final ConversionService conversionService;

    public DaoInvoker create(Method method) {
        MethodDetails details = new MethodDetails(method);

        ParameterExtractor extractor = details.parameterExtractor();

        ParamProvider paramProvider = paramProviderFactory.create(method);
        SqlProvider sqlProvider = sqlProviderFactory.createSql(method, paramProvider);
        RowMapper<?> rowMapper = rowMapper(method);

        if (details.isQueryList()) {
            return args -> {
                SqlParameterSource params = parameterSource(extractor, args);
                String sql = sqlProvider.createSql(params);
                return jdbcTemplate.query(sql, params, rowMapper);
            };
        }

        if (details.isUpdate()) {
            return args -> {
                SqlParameterSource params = parameterSource(extractor, args);
                String sql = sqlProvider.createSql(params);
                return jdbcTemplate.update(sql, params);
            };
        }

        if (details.isBatchUpdate()) {
            return args -> {
                SqlParameterSource[] paramsArray = parameterSourceArray(extractor, args);
                String sql = sqlProvider.createSql(new EmptySqlParameterSource());
                return jdbcTemplate.batchUpdate(sql, paramsArray);
            };
        }

        return args -> {
            SqlParameterSource params = parameterSource(extractor, args);
            String sql = sqlProvider.createSql(params);
            return jdbcTemplate.queryForObject(sql, params, rowMapper);
        };

    }

    private SqlParameterSource[] parameterSourceArray(ParameterExtractor extractor, Object[] args) {
        Collection<?> collection = (Collection<?>) args[0];

        return collection.stream()
            .map(arg -> parameterSource(extractor, new Object[] { arg }))
            .toArray(SqlParameterSource[]::new);
    }

    private SqlParameterSource parameterSource(ParameterExtractor extractor, Object[] args) {
        DaoSqlParameterSource daoSqlParameterSource = new DaoSqlParameterSource(dataSource);

        String[] names = extractor.names();
        Object[] values = extractor.values(args);

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object value = values[i];

            daoSqlParameterSource.addValue(name, value);
        }

        return daoSqlParameterSource;
    }


    private RowMapper<?> rowMapper(Method method) {
        Class<?> rowClass = DaoUtils.rowClass(method);

        if (DaoUtils.isBean(rowClass)) {
            return new ConstructorRowMapper<>(rowClass, conversionService);
        }

        return new ConvertingSingleColumnRowMapper<>(rowClass, conversionService);
    }
}
