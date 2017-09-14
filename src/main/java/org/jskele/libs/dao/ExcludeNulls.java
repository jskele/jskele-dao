package org.jskele.libs.dao;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When generating sql, don't update columns based on parameters, that are <code>null</code>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface ExcludeNulls {

}
