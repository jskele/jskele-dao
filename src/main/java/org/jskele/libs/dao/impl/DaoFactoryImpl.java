package org.jskele.libs.dao.impl;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.DaoFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.reflect.Reflection;

@RequiredArgsConstructor
public class DaoFactoryImpl implements DaoFactory {

	private final JdbcTemplate jdbcTemplate;
	private final ConversionService conversionService;

	@Override
	public <T extends Dao> T create(Class<T> dao) {
		DaoInvocationHandler handler = new DaoInvocationHandler(jdbcTemplate, conversionService, dao);
		return Reflection.newProxy(dao, handler);
	}
}
