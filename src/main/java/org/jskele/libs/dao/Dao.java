package org.jskele.libs.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.stereotype.Indexed;

@Indexed
@Target({TYPE})
@Retention(RUNTIME)
public @interface Dao {

  String schema() default "";

}
