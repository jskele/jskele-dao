package org.jskele.dao.impl.mappers;

import org.jskele.values.ValueClass;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapperUtils {

    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType)
            throws SQLException {

        if (ValueClass.class.isAssignableFrom(requiredType)) {
            return forValueClass(rs, index, requiredType);
        }

        return tryGetValue(rs, index, requiredType);
    }

    private static Object forValueClass(ResultSet rs, int index, Class<?> requiredType)
            throws SQLException {

        ResolvableType resolvableType = ResolvableType.forClass(ValueClass.class, requiredType);
        Class<?> valueType = checkNotNull(resolvableType.resolveGeneric(0));

        Object value = tryGetValue(rs, index, valueType);

        if (!valueType.isInstance(value)) {
            // if value is not of a standard type, then return the value and let conversionService handle it.
            return value;
        }

        try {
            return requiredType.getConstructor(valueType).newInstance(value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object tryGetValue(ResultSet rs, int index, Class<?> valueType) throws SQLException {
        try {
            return JdbcUtils.getResultSetValue(rs, index, valueType);
        } catch (ClassCastException | SQLException e) {
            // If getting the value as `valueType` fails, then fall back to string and let conversionService handle it.
            // This can happen for example if column type is 'text' but contains UUIDs and valueType is 'UUID.class',
            // then an ClassCastException will be thrown from Postgres JDBC driver.
            return rs.getString(index);
        }
    }
}
