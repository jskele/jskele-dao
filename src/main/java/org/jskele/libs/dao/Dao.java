package org.jskele.libs.dao;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Indexed
@Target({TYPE})
@Retention(RUNTIME)
public @interface Dao {

    String schema() default "";

}
