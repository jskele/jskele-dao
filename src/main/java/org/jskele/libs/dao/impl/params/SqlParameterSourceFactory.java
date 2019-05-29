package org.jskele.libs.dao.impl.params;

import lombok.RequiredArgsConstructor;
import org.jskele.libs.values.LongValue;
import org.jskele.libs.values.StringValue;
import org.jskele.libs.values.UuidValue;
import org.jskele.libs.values.ValueClass;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SqlParameterSourceFactory {

    private final DataSource dataSource;

    public SqlParameterSource[] createArray(ParameterExtractor extractor, Object[] args) {
        Collection<?> collection = (Collection<?>) args[0];

        return collection.stream()
                .map(arg -> create(extractor, new Object[]{arg}))
                .toArray(SqlParameterSource[]::new);
    }

    public SqlParameterSource create(ParameterExtractor extractor, Object[] args) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String[] names = extractor.names();
        Object[] values = extractor.values(args);

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object value = values[i];

            Object sqlValue = sqlValue(value);

            if (sqlValue instanceof String) {
                // Setting the parameter type of strings to `OTHER` will have the same effect as
                // the jdbc connection parameter `stringtype=unspecified`.
                // This moves the type detection and conversion to the server side, allowing us to use
                // non-standard types like `uuid` and `json` without any casting in SQLs or using PGObject values.
                parameterSource.addValue(name, sqlValue, Types.OTHER);
            } else {
                parameterSource.addValue(name, sqlValue);
            }
        }

        return parameterSource;
    }

    private Object sqlValue(Object value) {
        if (value instanceof Instant) {
            Instant instant = (Instant) value;
            return new Timestamp(instant.toEpochMilli());
        }

        if (value instanceof LocalDate) {
            return Date.valueOf((LocalDate) value);
        }

        if (value instanceof ValueClass) {
            return ((ValueClass) value).toValue();
        }

        if (value instanceof Enum) {
            return value.toString();
        }

        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isEmpty()) {
                return null;
            }

            Object object = collection.iterator().next();
            String typeName;

            if (object instanceof LocalDate) {
                typeName = "DATE";
            } else if (object instanceof Enum) {
                typeName = "TEXT";
            } else if (object instanceof String) {
                typeName = "TEXT";
            } else if (object instanceof Long) {
                typeName = "NUMERIC";
            } else if (object instanceof LongValue) {
                typeName = "BIGINT";
            } else if (object instanceof StringValue) {
                typeName = "TEXT";
            } else if (object instanceof UuidValue) {
                typeName = "UUID";
            } else if (object == null) {
                typeName = "TEXT";
            } else {
                throw new IllegalStateException("no typeName specified for " + object.getClass());
            }

            try (Connection connection = dataSource.getConnection()) {
                return connection.createArrayOf(typeName, collection.toArray());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return value;
    }
}
