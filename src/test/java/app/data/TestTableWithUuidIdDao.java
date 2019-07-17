package app.data;

import org.jskele.dao.CrudDao;
import org.jskele.dao.Dao;
import org.jskele.dao.ExcludeNulls;
import org.jskele.dao.GenerateSql;

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
