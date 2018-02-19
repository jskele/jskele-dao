package org.jskele.libs.dao.impl.params;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.values.LongValue;
import org.jskele.libs.values.StringValue;
import org.jskele.libs.values.ValueClass;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@RequiredArgsConstructor
public class DaoSqlParameterSource extends MapSqlParameterSource {

	private final DataSource dataSource;

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		Object value = super.getValue(paramName);

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

			try {
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
				} else if (object == null) {
					typeName = "TEXT";
				} else {
					throw new IllegalStateException("no typeName specified for " + object.getClass());
				}

				try (Connection connection = dataSource.getConnection()) {
					return connection.createArrayOf(typeName, collection.toArray());
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		return value;
	}
}
