package org.jskele.libs.dao.impl;

import java.beans.PropertyDescriptor;
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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.base.Preconditions;

@RequiredArgsConstructor
public class DaoSqlParameterSource extends MapSqlParameterSource {

	private final DataSource dataSource;

	public DaoSqlParameterSource() {
		this(null);
	}

	public DaoSqlParameterSource(DataSource dataSource, Object bean) {
		this(dataSource);
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] props = beanWrapper.getPropertyDescriptors();
		for (PropertyDescriptor prop : props) {
			String name = prop.getName();
			if (beanWrapper.isReadableProperty(name)) {
				addValue(name, beanWrapper.getPropertyValue(name));
			}
		}
	}

	public DaoSqlParameterSource(DataSource dataSource, String[] parameterNames, Object[] args) {
		this(dataSource);
		Preconditions.checkState(parameterNames.length == args.length,
				"Number of method parameters (%s) doesn't match number of arguments (%s)", parameterNames.length, args.length);

		for (int i = 0; i < args.length; i++) {
			String name = parameterNames[i];
			Object value = args[i];

			addValue(name, value);
		}
	}

	public DaoSqlParameterSource(DataSource dataSource, String paramName, Object value) {
		this(dataSource);
		addValue(paramName, value);
	}

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
