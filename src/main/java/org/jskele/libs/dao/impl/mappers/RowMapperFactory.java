package org.jskele.libs.dao.impl.mappers;

import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.DaoUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RowMapperFactory {

    private final ConversionService conversionService;

    public Supplier<RowMapper<?>> createSupplier(Method method, Class<?> daoClass) {
        Class<?> rowClass = DaoUtils.rowClass(method, daoClass);

        if (DaoUtils.isBean(rowClass)) {
            return () -> new ConstructorRowMapper<>(rowClass, conversionService);
        }

        return () -> new ConvertingSingleColumnRowMapper<>(rowClass, conversionService);
    }
}
