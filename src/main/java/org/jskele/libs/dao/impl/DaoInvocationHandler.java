package org.jskele.libs.dao.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.ExcludeNulls;
import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.IncludeNullsExcept;
import org.jskele.libs.dao.SqlTemplate;
import org.jskele.libs.values.LongValue;
import org.jskele.libs.values.ValueClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@Slf4j
class DaoInvocationHandler extends AbstractInvocationHandler {
	private static final ParameterNameDiscoverer PARAM_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
	private final Converter<String, String> converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedTemplate;
	private final ConversionService conversionService;
	private final Class<? extends Dao> daoClass;

	private static final PebbleEngine TEMPLATE_ENGINE = new PebbleEngine
			.Builder()
			.loader(new StringLoader())
			.autoEscaping(false)
			.build();

	DaoInvocationHandler(JdbcTemplate jdbcTemplate, ConversionService conversionService, Class<? extends Dao> daoClass) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		this.conversionService = conversionService;
		this.daoClass = daoClass;
	}

	@Override
	protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
		if (isInsert(method)) {
			return insert(method, args);
		}

		if (isDelete(method)) {
			return delete(method, args);
		}

		if (isUpdate(method)) {
			return update(method, args);
		}

		if (isCount(method)) {
			return count(method, args);
		}

		if (isExists(method)) {
			return exists(method, args);
		}

		if (isForList(method)) {
			return queryForList(method, args);
		}

		return queryForObject(method, args);
	}

	private Object update(Method method, Object[] args) throws IOException {
		String tableName = esc(tableName(method));
		MapSqlParameterSource paramSource = parameterSource(method, args);

		boolean excludeNulls = method.getAnnotation(ExcludeNulls.class) != null;
		String[] excludedColumns = null;
		IncludeNullsExcept includeNullsExcept = method.getAnnotation(IncludeNullsExcept.class);
		if (includeNullsExcept != null) {
			excludedColumns = includeNullsExcept.exceptParamNames();
		}
		String[] excluded = excludedColumns;

		String columns = paramSource.getValues().keySet().stream()
				.filter((name) -> !"id".equals(name))
				.filter((name) -> !"class".equals(name))
				.filter((name) -> includeParameter(excludeNulls, excluded, name, paramSource))
				.map(this::getSqlForColumnEquals)
				.collect(Collectors.joining(", "));

		String updateSql =
				getSqlFromFileOrGenerateFromMethod(method, paramSource,
						() -> "UPDATE " + tableName + " SET " + columns + " WHERE id = :id");

		return namedTemplate.update(updateSql, paramSource);
	}

	private boolean includeParameter(boolean excludeNulls, String[] columnNames, String name, MapSqlParameterSource paramSource) {
		if (columnNames != null && columnNames.length > 0 && Arrays.asList(columnNames).contains(name)) {
			return false;
		} else if (excludeNulls && paramSource.getValue(name) == null) {
			return false;
		} else {
			return true;
		}
	}

	private String getSqlForColumnEquals(String name) {
		return esc(converter.convert(name)) + " = :" + name;
	}

	private Object count(Method method, Object[] args) throws IOException {
		String tableName = esc(tableName(method));
		Class<?> returnType = method.getReturnType();
		Preconditions.checkArgument(method.getReturnType().isPrimitive(),
				"Expected primitive (int or long) as return type for %s, but got %s", method, method.getReturnType());
		MapSqlParameterSource paramSource = parameterSource(method, args);

		String sql = getSqlFromFileOrGenerateFromMethod(method,
				paramSource, () -> ("SELECT count(1) FROM " + tableName + sqlWhereAndParameters(method)));

		return namedTemplate.queryForObject(sql, paramSource, returnType);
	}

	private boolean exists(Method method, Object[] args) throws IOException {
		String tableName = esc(tableName(method));
		Preconditions.checkArgument(method.getReturnType().equals(boolean.class),
				"Expected primitive boolean as return type for %s, but got %s", method, method.getReturnType());
		MapSqlParameterSource paramSource = parameterSource(method, args);

		String sql = getSqlFromFileOrGenerateFromMethod(method,
				paramSource, () -> ("SELECT EXISTS(SELECT 1 FROM " + tableName + sqlWhereAndParameters(method) + ")"));

		return namedTemplate.queryForObject(sql, paramSource, boolean.class);
	}

	private boolean isUpdate(Method method) {
		return method.getName().startsWith("update");
	}

	private boolean isCount(Method method) {
		return method.getName().startsWith("count");
	}

	private boolean isExists(Method method) {
		return method.getName().startsWith("exists");
	}

	private Object delete(Method method, Object[] args) throws IOException {
		String tableName = tableName(method);
		MapSqlParameterSource paramSource = parameterSource(method, args);
		String deleteSql =
				getSqlFromFileOrGenerateFromMethod(method, paramSource,
						() -> "DELETE FROM " + tableName + sqlWhereAndParameters(method));

		return namedTemplate.update(deleteSql, paramSource);
	}

	private String tableName(Method method) {
		String daoName = daoClass.getSimpleName();
		String camelTableName = StringUtils.removeEnd(daoName, "Dao");

		String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelTableName);
		return tableName;
	}

	private boolean isDelete(Method method) {
		return method.getName().startsWith("delete");
	}

	private boolean isInsert(Method method) {
		return method.getName().startsWith("insert");
	}

	private Object insert(Method method, Object[] args) throws IOException {

		MapSqlParameterSource parameterSource = parameterSource(method, args);
		String sql = evaluateSqlFromFileWithMethodName(method, parameterSource);
		boolean hasGenerateSql = method.getAnnotation(GenerateSql.class) != null;
		if (sql != null) {
			checkState(!hasGenerateSql,
					"Method %s has @GenerateSql annotation, but also SQL file in classpath, please remove one of them.", method);

			if (isBatch(method)) {
				return namedTemplate.batchUpdate(sql, batchParamSources(args));
			}

			return namedTemplate.update(sql, parameterSource);
		} else {
			checkState(hasGenerateSql,
					"Method %s does NOT have @GenerateSql annotation, but also does NOT have SQL file in classpath, please add one of them.",
					method);
		}

		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		Object bean = args[0];
		String tableName = tableName(method);

		boolean numericId = isNumericId(bean);
		try {
			String[] columns = columnNames(beanClass(method))
					.map(converter::convert)
					.filter(column -> !(numericId && column.equals("id")))
					.toArray(String[]::new);
			insert = insert
					.withTableName(esc(tableName))
					.usingColumns(columns);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

		if (isBatch(method)) {
			DaoSqlParameterSource[] paramSources = batchParamSources(args);
			insert.executeBatch(paramSources);
			return null;
		}

		parameterSource = new DaoSqlParameterSource(jdbcTemplate.getDataSource(), bean);

		if (numericId) {
			Number id = insert.usingGeneratedKeyColumns("id").executeAndReturnKey(parameterSource);
			return conversionService.convert(id, rowClass(method));
		}

		insert.execute(parameterSource);
		return null;
	}

	private DaoSqlParameterSource[] batchParamSources(Object[] args) {
		List<?> list = (List<?>) args[0];
		return list.stream()
				.map(it -> new DaoSqlParameterSource(jdbcTemplate.getDataSource(), it))
				.toArray(DaoSqlParameterSource[]::new);
	}

	private boolean isNumericId(Object bean) {
		Class<?> idClass;
		try {
			idClass = PropertyUtils.getPropertyType(bean, "id");
		} catch (Exception e) {
			return false;
		}

		if (idClass == null) {
			return false;
		}

		if (LongValue.class.isAssignableFrom(idClass)) {
			return true;
		}

		return Number.class.isAssignableFrom(idClass);
	}

	private boolean isForList(Method method) {
		return method.getReturnType().equals(List.class);
	}

	private List<?> queryForList(Method method, Object[] args) throws IOException {
		MapSqlParameterSource parameterSource = parameterSource(method, args);
		String sqlString = getSqlFromFileOrGenerateFromMethod(method, parameterSource, () -> generateSelectSql(method));
		RowMapper<?> rowMapper = rowMapper(method);

		String methodName = method.getName();
		if (methodName.startsWith("select") || methodName.startsWith("get") || methodName.startsWith("find")) {
			return namedTemplate.query(sqlString, parameterSource, rowMapper);
		}

		int update = namedTemplate.update(sqlString, parameterSource);
		return ImmutableList.of(update);
	}

	private Object queryForObject(Method method, Object[] args) throws IOException {
		List<?> list = queryForList(method, args);

		Object object;
		if (list.isEmpty()) {
			object = null;
		} else {
			object = list.get(0);
		}

		if (method.getReturnType().equals(Optional.class)) {
			return Optional.ofNullable(object);
		}

		return object;
	}

	private String getSqlFromFileOrGenerateFromMethod(
			Method methodForSqlFile,
			MapSqlParameterSource paramSource, Supplier<String> fallbackForAutoGeneratedSql) throws IOException {
		String sql = evaluateSqlFromFileWithMethodName(methodForSqlFile, paramSource);
		boolean hasGenerateSql = methodForSqlFile.getAnnotation(GenerateSql.class) != null;
		if (sql != null) {
			checkState(!hasGenerateSql,
					"Method %s HAS @GenerateSql annotation, and also HAS SQL file in classpath, please remove one of them.",
					methodForSqlFile);
			return sql;
		}
		checkState(hasGenerateSql,
				"Method %s does NOT have @GenerateSql annotation, but also does NOT have SQL file in classpath, please add one of them.",
				methodForSqlFile);
		return fallbackForAutoGeneratedSql.get();
	}

	private String evaluateSqlFromFileWithMethodName(Method method, MapSqlParameterSource paramSource) throws
			IOException {
		boolean isTemplate = method.getAnnotation(SqlTemplate.class) != null;
		String sqlFileName = getSqlFileName(method);
		if (isTemplate) {
			sqlFileName += ".peb";
		}
		// creating and evaluating simple template takes ca 1ms (on dev laptop)
		String sql = getSqlFromFileWithMethodName(sqlFileName, paramSource);
		if (!isTemplate) {
			return sql;
		}
		return evaluateSqlTemplate(sql, paramSource);
	}

	private String evaluateSqlTemplate(String sql, MapSqlParameterSource paramSource) {
		PebbleTemplate compiledTemplate;
		try {
			compiledTemplate = TEMPLATE_ENGINE.getTemplate(sql);
		} catch (PebbleException e) {
			throw Throwables.propagate(e);
		}
		Writer writer = new StringWriter();
		try {
			// need modifiable map, as for loop add implicit variables (chosen variable + "loop" state object)
			Map<String, Object> templateParams = new HashMap<>(paramSource.getValues());
			compiledTemplate.evaluate(writer, templateParams);
		} catch (PebbleException | IOException e) {
			throw Throwables.propagate(e);
		}

		return writer.toString();
	}

	private String getSqlFromFileWithMethodName(String sqlFileName, SqlParameterSource paramSource) throws IOException {
		URL sqlUrl = getClass().getResource(sqlFileName);
		return sqlUrl == null ? null : Resources.toString(sqlUrl, Charsets.UTF_8);
	}

	private String getSqlFileName(Method method) {
		String packageName = Reflection.getPackageName(method.getDeclaringClass());
		String methodName = method.getName();
		return "/" + packageName.replace(".", "/") + "/" + methodName + ".sql";
	}

	private String generateSelectSql(Method method) {
		Class<?> rowClass = rowClass(method);
		Stream<String> columnNames = columnNames(rowClass);

		String sqlFields = columnNames
				.map(converter::convert)
				.map(this::esc)
				.collect(Collectors.joining(", "));

		String tableName = tableName(method);

		return "SELECT " + sqlFields + " FROM " + esc(tableName) + sqlWhereAndParameters(method);
	}

	private String esc(String s) {
		return '"' + s + '"';
	}

	private Stream<String> columnNames(Class<?> rowClass) {
		String[] names = ConstructorRowMapper
				.findConstructor(rowClass)
				.getAnnotation(ConstructorProperties.class)
				.value();

		return Arrays.stream(names);
	}

	private String sqlWhereAndParameters(Method method) {
		String s = paramName(method);
		if (s == null) {
			String[] paramNamesFromArguments = getParamNamesFromArguments(method);
			if (paramNamesFromArguments.length == 0) {
				return "";
			}
			String predicates = Lists.newArrayList(paramNamesFromArguments).stream()
					.map(this::getSqlForColumnEquals)
					.collect(Collectors.joining(" AND "));
			return " WHERE " + predicates;
		}
		return " WHERE " + esc(converter.convert(s)) + " = :" + s;
	}

	private MapSqlParameterSource parameterSource(Method method, Object[] args) {
		DataSource dataSource = jdbcTemplate.getDataSource();
		if (args.length == 0) {
			return new DaoSqlParameterSource();
		}

		if (args.length == 1 && isBean(method.getParameterTypes()[0])) {
			return new DaoSqlParameterSource(dataSource, args[0]);
		}

		if (args.length == 1 && method.getName().contains("By")) {
			String paramName = paramName(method);
			return new DaoSqlParameterSource(dataSource, paramName, args[0]);
		}

		return new DaoSqlParameterSource(dataSource, getParamNamesFromArguments(method), args);
	}

	private boolean isBean(Class<?> clazz) {
		if (clazz.equals(Object.class)) {
			return false;
		}

		if (ValueClass.class.isAssignableFrom(clazz)) {
			return false;
		}

		if (Temporal.class.isAssignableFrom(clazz)) {
			return false;
		}

		if (String.class.isAssignableFrom(clazz)) {
			return false;
		}

		if (Enum.class.isAssignableFrom(clazz)) {
			return false;
		}

		if (ClassUtils.isPrimitiveOrWrapper(clazz)) {
			return false;
		}

		return true;
	}

	private String[] getParamNamesFromArguments(Method method) {
		String[] parameterNames = PARAM_NAME_DISCOVERER.getParameterNames(method);
		Preconditions.checkNotNull(parameterNames,
				"Failed to discover method parameter names for method %s. Add '-parameters' compiler option", method);
		return parameterNames;
	}

	private String paramName(Method method) {
		String methodName = method.getName();
		int byIndex = methodName.lastIndexOf("By");
		if (byIndex == -1) {
			return null;
		}

		String camel = methodName.substring(byIndex + "By".length());
		Converter<String, String> converter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL);
		return converter.convert(camel);
	}

	private RowMapper<?> rowMapper(Method method) {
		Class<?> beanClass = rowClass(method);

		if (!isBean(beanClass)) {
			return new ConvertingSingleColumnRowMapper<>(beanClass, conversionService);
		}

		return new ConstructorRowMapper<>(beanClass, conversionService);
	}

	private Class<?> rowClass(Method method) {
		ResolvableType resolvableType = ResolvableType.forMethodReturnType(method, daoClass);

		if (resolvableType.hasGenerics()) {
			ResolvableType[] generics = resolvableType.getGenerics();
			checkArgument(generics.length == 1);
			return generics[0].resolve();
		}

		return resolvableType.resolve();
	}

	private Class<?> beanClass(Method method) {
		ResolvableType resolvableType = ResolvableType.forMethodParameter(method, 0, daoClass);

		if (resolvableType.hasGenerics()) {
			ResolvableType[] generics = resolvableType.getGenerics();
			checkArgument(generics.length == 1);
			return generics[0].resolve();
		}

		return resolvableType.resolve();
	}

	private boolean isBatch(Method method) {
		return method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(List.class);
	}
}
