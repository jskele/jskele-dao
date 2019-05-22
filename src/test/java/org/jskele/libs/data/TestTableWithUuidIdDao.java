package org.jskele.libs.data;

import org.jskele.libs.dao.CrudDao;
import org.jskele.libs.dao.Dao;

@Dao
public interface TestTableWithUuidIdDao extends CrudDao<TestTableWithUuidIdRow, TestTableWithUuidIdRowId> {
}
