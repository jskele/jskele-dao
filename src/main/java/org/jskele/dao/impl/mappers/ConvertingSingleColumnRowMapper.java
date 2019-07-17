package org.jskele.dao.impl.mappers;

import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConvertingSingleColumnRowMapper<T> extends SingleColumnRowMapper<T> {

    public ConvertingSingleColumnRowMapper(Class<T> requiredType, ConversionService conversionService) {
        super(requiredType);
        this.setConversionService(conversionService);
    }

    @Override
    protected Object getColumnValue(ResultSet rs, int index, Class<?> requiredType)
        throws SQLException {
        return MapperUtils.getResultSetValue(rs, index, requiredType);
    }
}
