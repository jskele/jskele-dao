package org.jskele.libs.dao;

public interface DbSchemaResolver {
    /**
     * @return DB shema based on given Dao class (annotated with `@Dao`)
     */
    String resolve(Class<?> daoClass);
}
