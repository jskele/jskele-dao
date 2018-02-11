package org.jskele.libs.dao.impl2.sql;

import static java.util.stream.Collectors.joining;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.jskele.libs.dao.GenerateSql;
import org.jskele.libs.dao.impl2.DaoUtils;
import org.jskele.libs.dao.impl2.params.ParamProvider;
import org.jskele.libs.values.LongValue;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;

@RequiredArgsConstructor
class GeneratedSqlSource implements SqlProvider {
    private final Converter<String, String> CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
    private final Method method;
    private final ParamProvider paramProvider;
    private boolean numericId;

    public boolean isPresent() {
        GenerateSql annotation = method.getAnnotation(GenerateSql.class);
        return annotation != null;
    }

    @Override
    public String getSql(SqlParameterSource parameterSource) {

        if (isSelect()) {
            return generateSelect();
        }

        if (isDelete()) {
            return generateDelete();
        }

        if (isInsert()) {
            return generateInsert();
        }

        if (isUpdate()) {
            return generateUpdate();
        }

        if (hasPrefix("exists")) {
            return generateExists();
        }

        if (hasPrefix("count")) {
            return generateCount();
        }

        return null;
    }

    private String generateUpdate() {
        return "UPDATE " + tableName() + " SET " + updateColumns() + " WHERE id = :id";
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
        String[] paramNames = paramProvider.getNames();
        return Arrays.stream(paramNames)
            .filter(name -> !(isNumericId() && name.equals("id")))
            .map(name -> ":" + name)
            .collect(joining(", "));
    }


    private String updateColumns() {
        String[] paramNames = paramProvider.getNames();
        return Arrays.stream(paramNames)
            .filter(name -> !name.equals("id"))
            .map(this::columnEqualsParameter)
            .collect(joining(", "));
    }

    private String insertColumns() {
        String[] paramNames = paramProvider.getNames();
        return Arrays.stream(paramNames)
            .filter(name -> !(isNumericId() && name.equals("id")))
            .map(this::convert)
            .map(this::esc)
            .collect(joining(", "));
    }

    private String selectColumns() {
        Class<?> rowClass = DaoUtils.rowClass(method);
        String[] columnNames = DaoUtils.constructorProperties(rowClass);

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
        String[] paramNames = paramProvider.getNames();

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
        String daoName = method.getDeclaringClass().getSimpleName();
        String camelTableName = StringUtils.removeEnd(daoName, "Dao");

        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelTableName);
        return esc(tableName);
    }

    public boolean isSelect() {
        return hasPrefix("select");
    }

    public boolean isDelete() {
        return hasPrefix("delete");
    }

    private boolean hasPrefix(String prefix) {
        return method.getName().startsWith(prefix);
    }

    public boolean isInsert() {
        return hasPrefix("insert");
    }

    public boolean isUpdate() {
        return hasPrefix("update");
    }


    private boolean isNumericId() {
        Class<?> returnType = method.getReturnType();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(returnType);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        Class<?> idClass = Arrays.stream(beanInfo.getPropertyDescriptors())
            .filter(pd -> pd.getName().equals("id"))
            .map(PropertyDescriptor::getPropertyType)
            .findAny()
            .orElse(null);

        if (idClass == null) {
            return false;
        }

        if (LongValue.class.isAssignableFrom(idClass)) {
            return true;
        }

        return Number.class.isAssignableFrom(idClass);
    }
}
