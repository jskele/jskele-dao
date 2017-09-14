package org.jskele.libs.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate that sql file content should be evaluated with template
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface SqlTemplate {

}
