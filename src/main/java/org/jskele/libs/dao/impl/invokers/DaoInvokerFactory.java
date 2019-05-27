package org.jskele.libs.dao.impl.invokers;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.DaoUtils;
import org.jskele.libs.dao.impl.MethodDetails;
import org.jskele.libs.dao.impl.mappers.ConstructorRowMapper;
import org.jskele.libs.dao.impl.mappers.ConvertingSingleColumnRowMapper;
import org.jskele.libs.dao.impl.params.DaoSqlParameterSource;
import org.jskele.libs.dao.impl.params.ParameterExtractor;
import org.jskele.libs.dao.impl.sql.SqlSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final DataSource dataSource;

	private final ConversionService conversionService;

	public DaoInvoker create(Method method, Class<?> daoClass) {
		MethodDetails details = new MethodDetails(method);

		ParameterExtractor extractor = ParameterExtractor.create(method, daoClass);
		Preconditions.checkState(extractor.names() != null,
				"Parameter names for generating SQL base based on %s can't be resolved. Try adding compiler arguments `-parameters`",
				method);

		boolean isBatchInsertOrUpdate = details.isBatchUpdate();
		SqlSource sqlSource = SqlSource.create(daoClass, method, extractor,
				isBatchInsertOrUpdate);

		if (details.isUpdate()) {
			return args -> {
				SqlParameterSource params = parameterSource(extractor, args);
				String sql = sqlSource.generateSql(args);
				return jdbcTemplate.update(sql, params);
			};
		}

		if (isBatchInsertOrUpdate) {
			return args -> {
				SqlParameterSource[] paramsArray = parameterSourceArray(extractor, args);
				String sql = sqlSource.generateSql(args);
				return jdbcTemplate.batchUpdate(sql, paramsArray);
			};
		}

		RowMapper<?> rowMapper = rowMapper(method, daoClass);

		if (details.isQueryList()) {
			return args -> {
				SqlParameterSource params = parameterSource(extractor, args);
				String sql = sqlSource.generateSql(args);
				return jdbcTemplate.query(sql, params, rowMapper);
			};
		}

		return args -> {
			SqlParameterSource params = parameterSource(extractor, args);
			String sql = sqlSource.generateSql(args);
			return jdbcTemplate.queryForObject(sql, params, rowMapper);
		};

	}

	private SqlParameterSource[] parameterSourceArray(ParameterExtractor extractor,
			Object[] args) {
		Collection<?> collection = (Collection<?>) args[0];

		return collection.stream()
				.map(arg -> parameterSource(extractor, new Object[] { arg }))
				.toArray(SqlParameterSource[]::new);
	}

	private SqlParameterSource parameterSource(ParameterExtractor extractor,
			Object[] args) {
		DaoSqlParameterSource daoSqlParameterSource = new DaoSqlParameterSource(
				dataSource);

		String[] names = extractor.names();
		Object[] values = extractor.values(args);

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			Object value = values[i];

			daoSqlParameterSource.addValue(name, value);
		}

		return daoSqlParameterSource;
	}

	private RowMapper<?> rowMapper(Method method, Class<?> daoClass) {
		Class<?> rowClass = DaoUtils.rowClass(method, daoClass);

		if (DaoUtils.isBean(rowClass)) {
			return new ConstructorRowMapper<>(rowClass, conversionService);
		}

		return new ConvertingSingleColumnRowMapper<>(rowClass, conversionService);
	}

}
