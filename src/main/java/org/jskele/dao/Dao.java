package org.jskele.dao;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Indexed
@Target({TYPE})
@Retention(RUNTIME)
public @interface Dao {

    /**
     * With default configuration it can be used to specify DB schema to be used by the Dao class.
     * Default behaviour can be overridden using custom {@link DbSchemaResolver} bean.
     */
    String schema() default "";

}
