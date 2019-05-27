package org.jskele.libs.dao.impl.mappers;

import com.google.common.base.Preconditions;
import org.jskele.libs.dao.JsonValue;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;

public class JsonValueConverter implements Converter<org.postgresql.util.PGobject, JsonValue> {

    @Override
    public JsonValue convert(PGobject source) {
        String value = source.getValue();
        Preconditions.checkArgument(canConvert(source), "Can't convert from " + source.getType() + ": " + value);
        return new JsonValue(value);
    }

    boolean canConvert(PGobject source) {
        return source.getType().equals("json");
    }
}