package org.jskele.libs.dao;

public interface DaoFactory {
	<T extends Dao> T create(Class<T> dao);
}
