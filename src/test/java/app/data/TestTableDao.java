package app.data;

import java.util.List;

import org.jskele.libs.dao.CrudDao;
import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.GenerateSql;

@Dao
public interface TestTableDao extends CrudDao<TestTableRow, TestTableRowId> {

    @GenerateSql
    List<TestTableRow> selectAll();

    @GenerateSql
    int[] insertBatch(List<TestTableRow> rows);

    @GenerateSql
    int[] updateBatch(List<TestTableRow> rows);

    @GenerateSql
    TestTableRow selectForUpdate(TestTableRowId id);

    List<TestTableRow> findByNumericColumnIn(String excludedValue, List<Long> numericColumns);

    TestTableRow findByStringColumn(String stringColumn);
}
