package org.jskele.libs.dao.impl2.mappers;

import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.SingleColumnRowMapper;

class ConvertingSingleColumnRowMapper<T> extends SingleColumnRowMapper<T> {

	private final ConversionService conversionService;

	public ConvertingSingleColumnRowMapper(Class<T> requiredType, ConversionService conversionService) {
		super(requiredType);
		this.conversionService = conversionService;
	}

	@Override
	protected Object convertValueToRequiredType(Object value, Class<?> requiredType) {
		return conversionService.convert(value, requiredType);
	}
}
