package org.jskele.libs.dao.impl.mappers;

import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.DaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RowMapperFactory {

    private static final ConversionService defaultConversionService = DefaultConversionService.getSharedInstance();

    @Autowired(required = false)
    private ConversionService conversionService;

    // XXX: maybe add the possibility do define a specific custom conversionService bean,
    // like in spring-session (see JdbcHttpSessionConfiguration)

    public Supplier<RowMapper<?>> createSupplier(Method method, Class<?> daoClass) {
        Class<?> rowClass = DaoUtils.rowClass(method, daoClass);

        if (DaoUtils.isBean(rowClass)) {
            return () -> new ConstructorRowMapper<>(rowClass, conversionService());
        }

        return () -> new ConvertingSingleColumnRowMapper<>(rowClass, conversionService());
    }

    private ConversionService conversionService() {
        if (conversionService != null) {
            return conversionService;
        }

        return defaultConversionService;
    }
}
