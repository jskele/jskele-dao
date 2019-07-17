package org.jskele.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker annotation to indicate that sql file content should be evaluated with template
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface SqlTemplate {

}
