package org.jskele.libs.dao.impl.sql;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.ExcludeNulls;
import org.jskele.libs.dao.impl.DaoUtils;
import org.jskele.libs.dao.impl.params.ParameterExtractor;
import org.jskele.libs.values.LongValue;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.jskele.libs.dao.impl.DaoUtils.hasAnnotation;

@RequiredArgsConstructor
class SqlGenerator {
    private static final Converter<String, String> CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    private final Class<?> daoClass;
    private final Method method;
    private final ParameterExtractor extractor;

    public SqlSource createSource(boolean isBatchInsertOrUpdate) {
        if (hasPrefix("delete")) {
            return staticSqlSource(generateDelete());
        }

        if (hasPrefix("insert")) {
            return args -> generateInsert(args, isBatchInsertOrUpdate);
        }

        if (hasPrefix("update")) {
            return this::generateUpdate;
        }

        if (hasPrefix("exists")) {
            return staticSqlSource(generateExists());
        }

        if (hasPrefix("count")) {
            return staticSqlSource(generateCount());
        }

        if (hasPrefix("selectForUpdate")) {
            return staticSqlSource(generateSelectForUpdate());
        }

        return staticSqlSource(generateSelect());
    }

    private SqlSource staticSqlSource(String sql) {
        return args -> sql;
    }

    private String generateUpdate(Object[] args) {
        return "UPDATE " + tableName() + " SET " + updateColumns(args) + " WHERE id = :id";
    }

    private String generateCount() {
        return "SELECT count(*) FROM" + tableName() + whereCondition();
    }

    private String generateExists() {
        return "SELECT EXISTS(SELECT 1 FROM " + tableName() + whereCondition() + ")";
    }

    private String generateInsert(Object[] args, boolean isBatchInsert) {
        if (isBatchInsert) {
            // XXX: same sql needs to be used for all batch rows - using first row to generate that SQL.
            // NB! Note that it may be unexpected that for some consecutive row another SQL could be created
            // (for example if first row has id set, but second row has no id, then id of second row would be ignored during insert and taken from sequence instead)
            // As it will probably be fairly uncommon situation,
            // then we don't support detecting that problem right now
            // (or solving it by splitting single batch into multiple batch statements).
            Collection<?> batchArgsList = (Collection) args[0];
            Preconditions.checkState(!batchArgsList.isEmpty(), "Didn't expect empty collection for batch operation");
            Object firstRowArgs = batchArgsList.iterator().next();
            return generateInsert(new Object[]{firstRowArgs}, false);
        }
        Map<String, Object> paramValuesByName = getParamValuesByName(args);
        return "INSERT INTO " + tableName() + " (" + insertColumns(paramValuesByName) + ")" +
                " VALUES (" + insertValues(paramValuesByName) + ")" + insertReturning();
    }

    private String insertReturning() {
        Class<?> idClass = extractor.getTypeOf("id");
        if (idClass != null && (isNumericId(idClass) || method.getReturnType().isAssignableFrom(idClass))) {
            return " RETURNING id";
        }

        return "";
    }

    private String generateDelete() {
        return "DELETE FROM " + tableName() + whereCondition();
    }

    private String generateSelect() {
        return "SELECT " + selectColumns() + " FROM " + tableName() + whereCondition();
    }

    private String generateSelectForUpdate() {
        return generateSelect() + " FOR UPDATE";
    }

    private String insertValues(Map<String, Object> paramValuesByName) {
        return getParamNamesWithoutIdIfIdValueIsNull(paramValuesByName)
                .map(name -> ":" + name)
                .collect(joining(", "));
    }

    private String updateColumns(Object[] args) {
        String[] paramNames = updateParamNames(args);

        return Arrays.stream(paramNames)
                .filter(name -> !name.equals("id"))
                .map(this::columnEqualsParameter)
                .collect(joining(", "));
    }

    private String[] updateParamNames(Object[] args) {
        String[] names = extractor.names();
        if (!hasAnnotation(method, ExcludeNulls.class)) {
            return names;
        }

        return getParamNamesWithNotNullValues(names, args);
    }

    private String[] getParamNamesWithNotNullValues(String[] names, Object[] args) {
        Object[] values = extractor.values(args);

        return IntStream.range(0, names.length)
                .filter(i -> values[i] != null)
                .mapToObj(i -> names[i])
                .toArray(String[]::new);
    }

    private Stream<String> getParamNamesWithoutIdIfIdValueIsNull(Map<String, Object> paramValuesByName) {
        return paramValuesByName.entrySet().stream()
                .filter(entry -> isParamNameNotIdOrIdWithValue(entry.getKey(), entry.getValue()))
                .map(Map.Entry::getKey);
    }

    private Map<String, Object> getParamValuesByName(Object[] args) {
        String[] paramNames = extractor.names();
        Object[] values = extractor.values(args);
        // Not using
        // `IntStream.range(0, paramNames.length).boxed().collect(toMap(i -> paramNames[i], i -> values[i]));`
        // as it would throw NPE when value is null
        Map<String, Object> paramValuesByName = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramValuesByName.put(paramNames[i], values[i]) != null) {
                throw new IllegalStateException("Duplicate parameter name!");
            }
        }
        return paramValuesByName;
    }

    private boolean isParamNameNotIdOrIdWithValue(String paramName, Object paramValue) {
        if ("id".equals(paramName)) {
            return paramValue != null;
        }
        return true;
    }

    private String insertColumns(Map<String, Object> paramValuesByName) {
        return getParamNamesWithoutIdIfIdValueIsNull(paramValuesByName)
                .map(this::convert)
                .map(this::esc)
                .collect(joining(", "));
    }

    private String selectColumns() {
        Class<?> rowClass = DaoUtils.rowClass(method, daoClass);
        String[] columnNames = DaoUtils.beanProperties(rowClass);

        return Arrays.stream(columnNames)
                .map(this::convert)
                .map(this::esc)
                .collect(joining(", "));
    }

    private String convert(String s) {
        return CONVERTER.convert(s);
    }

    private String esc(String s) {
        return '"' + s + '"';
    }

    private String whereCondition() {
        String[] paramNames = extractor.names();

        if (paramNames.length == 0) {
            return "";
        }

        String predicates = Arrays.stream(paramNames)
                .map(this::columnEqualsParameter)
                .collect(joining(" AND "));

        return " WHERE " + predicates;
    }

    private String columnEqualsParameter(String name) {
        return esc(convert(name)) + " = :" + name;
    }

    private String tableName() {
        String daoName = daoClass.getSimpleName();
        String camelTableName = StringUtils.removeEnd(daoName, "Dao");

        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelTableName);

        String schema = detectSchema();

        String prefix = StringUtils.isBlank(schema) ? "" : esc(schema) + ".";
        return prefix + esc(tableName);
    }

    private String detectSchema() {
        Dao annotation = AnnotationUtils.findAnnotation(daoClass, Dao.class);
        String daoSpecificSchema = annotation.schema();
        // TODO allow specifying default schema, so that it wouldn't need to be set for every Dao individually
        return daoSpecificSchema;
    }

    private boolean hasPrefix(String prefix) {
        return method.getName().startsWith(prefix);
    }

    private boolean isNumericId(Class<?> idClass) {
        if (LongValue.class.isAssignableFrom(idClass)) {
            return true;
        }

        return Number.class.isAssignableFrom(idClass);
    }

}
