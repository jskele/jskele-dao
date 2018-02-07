package org.jskele.libs.dao;

public interface DaoFactory {

	String BEAN_NAME = "daoFactory";
	String METHOD_NAME = "create";

	<T extends Dao> T create(Class<T> dao);
}
