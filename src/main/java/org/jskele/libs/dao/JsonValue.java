package org.jskele.libs.dao;

import org.jskele.values.StringValue;

/**
 * JSON columns need special converting -
 * this class can be used (as a base class) for fields that correspond
 * to Postgres `json` DB column type
 */
public class JsonValue extends StringValue {
    public JsonValue(String value) {
        super(value);
    }
}
