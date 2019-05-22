package org.jskele.libs.dao.impl.mappers;

import org.jskele.libs.dao.JsonValue;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LibDaoConversionService extends DefaultConversionService {
    private final JsonValueConverter jsonValueConverter = new JsonValueConverter();

    @Override
    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (super.canConvert(sourceType, targetType)) {
            return true;
        }
        return canConvertPGobjectToJsonValue(null, sourceType, targetType);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return super.convert(source, sourceType, targetType);
        } catch (ConverterNotFoundException e) {
            if (canConvertPGobjectToJsonValue(source, sourceType, targetType)) {
                // This happens when reading from DB when column is `json`
                // and value is instance (possibly even subclass) of JsonValue
                return convertPGobjectToJsonValue((PGobject) source, targetType);
            }
            throw e;
        }
    }

    private boolean canConvertPGobjectToJsonValue(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> sourceClass = sourceType.getType();
        return sourceClass.equals(PGobject.class)
                && JsonValue.class.isAssignableFrom(targetType.getType())
                && (source == null || jsonValueConverter.canConvert((PGobject) source));
    }

    private Object convertPGobjectToJsonValue(PGobject source, TypeDescriptor targetType) {
        JsonValue jsonValue = jsonValueConverter.convert(source);
        Class<?> targetClass = targetType.getType();
        try {
            Constructor<?> constructor = targetClass.getConstructor(String.class);
            return constructor.newInstance(jsonValue.toValue());
        } catch (NoSuchMethodException nce) {
            throw new RuntimeException("Can't construct instance of " + targetClass.getName() + " as constructor with String is missing", nce);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Can't construct instance of " + targetClass.getName() + " as constructor isn't accessible", iae);
        } catch (InstantiationException | InvocationTargetException e1) {
            throw new RuntimeException(e1);
        }
    }

}
