package app.data;

import org.jskele.libs.dao.CrudDao;
import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.ExcludeNulls;
import org.jskele.libs.dao.GenerateSql;

@Dao
public interface TestTableWithUuidIdDao extends CrudDao<TestTableWithUuidIdRow, TestTableWithUuidIdRowId> {
    @GenerateSql
    void insertWithVoidReturnType(TestTableWithUuidIdRow row);

    @GenerateSql
    @ExcludeNulls
    void updateMethodWithVoidReturnType(TestTableWithUuidIdRow withStringColumn);

    @GenerateSql
    void deleteWithVoidReturnType(TestTableWithUuidIdRowId id);
}
