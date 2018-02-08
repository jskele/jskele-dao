package org.jskele.libs.dao.impl2.mappers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.DaoUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RowMapperFactory {

    private final ConversionService conversionService;

    public RowMapper<?> create(Method method) {
        Class<?> rowClass = DaoUtils.rowClass(method);

        if (DaoUtils.isBean(rowClass)) {
            return new ConstructorRowMapper<>(rowClass, conversionService);
        }

        return new ConvertingSingleColumnRowMapper<>(rowClass, conversionService);
    }
}
