package org.jskele.libs.dao.impl2.sql;

import static java.util.stream.Collectors.joining;
import static org.jskele.libs.dao.impl2.DaoUtils.hasAnnotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.jskele.libs.dao.ExcludeNulls;
import org.jskele.libs.dao.impl2.DaoUtils;
import org.jskele.libs.dao.impl2.params.ParameterExtractor;
import org.jskele.libs.values.LongValue;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

@RequiredArgsConstructor
class SqlGenerator {
    private static final Converter<String, String> CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    private final Method method;
    private final ParameterExtractor extractor;

    public SqlSource createSource() {
        if (hasPrefix("delete")) {
            return staticSqlSource(generateDelete());
        }

        if (hasPrefix("insert")) {
            return staticSqlSource(generateInsert());
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

    private String generateInsert() {
        return "INSERT INTO " + tableName() + " (" + insertColumns() + ") VALUES (" + insertValues() + ")" + insertReturning();
    }

    private String insertReturning() {
        if (isNumericId()) {
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

    private String insertValues() {
        String[] paramNames = extractor.names();
        return Arrays.stream(paramNames)
            .filter(this::notGeneratedColumn)
            .map(name -> ":" + name)
            .collect(joining(", "));
    }

    private String updateColumns(Object[] args) {
        Object[] values = extractor.values(args);
        String[] paramNames = extractor.names();

        paramNames = excludeNulls(paramNames, values);

        return Arrays.stream(paramNames)
            .filter(name -> !name.equals("id"))
            .map(this::columnEqualsParameter)
            .collect(joining(", "));
    }

    private String[] excludeNulls(String[] names, Object[] values) {
        if (hasAnnotation(method, ExcludeNulls.class)) {
            return names;
        }

        return IntStream.range(0, names.length)
            .filter(i -> values[i] != null)
            .mapToObj(i -> names[i])
            .toArray(String[]::new);
    }

    private String insertColumns() {
        String[] paramNames = extractor.names();
        return Arrays.stream(paramNames)
            .filter(this::notGeneratedColumn)
            .map(this::convert)
            .map(this::esc)
            .collect(joining(", "));
    }

    private String selectColumns() {
        Class<?> rowClass = DaoUtils.rowClass(method);
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


    private boolean notGeneratedColumn(String name) {
        return !(isNumericId() && name.equals("id"));
    }

    private String tableName() {
        String daoName = method.getDeclaringClass().getSimpleName();
        String camelTableName = StringUtils.removeEnd(daoName, "Dao");

        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelTableName);
        return esc(tableName);
    }

    private boolean hasPrefix(String prefix) {
        return method.getName().startsWith(prefix);
    }

    private boolean isNumericId() {
        int idIndex = Arrays.asList(extractor.names()).indexOf("id");

        if (idIndex == -1) {
            return false;
        }

        Class<?> idClass = extractor.types()[idIndex];

        if (LongValue.class.isAssignableFrom(idClass)) {
            return true;
        }

        return Number.class.isAssignableFrom(idClass);
    }

}
