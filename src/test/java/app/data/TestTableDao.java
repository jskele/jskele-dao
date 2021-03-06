package app.data;

import app.data.TestTableRow.JsonColumn;
import org.jskele.dao.CrudDao;
import org.jskele.dao.Dao;
import org.jskele.dao.GenerateSql;
import org.jskele.dao.SqlTemplate;

import java.util.List;

@Dao
public interface TestTableDao extends CrudDao<TestTableRow, TestTableRowId> {

    @GenerateSql
    List<TestTableRow> selectAll();

    @GenerateSql
    int[] updateBatch(List<TestTableRow> rows);

    @GenerateSql
    TestTableRow selectForUpdate(TestTableRowId id);

    List<TestTableRow> findByNumericColumnIn(String excludedValue, List<Long> numericColumns);

    TestTableRow findByStringColumn(String stringColumn);

    @SqlTemplate
    TestTableRow findByStringColumnAndNullableNumericColumn(String stringColumn, Integer numericColumn);

    /**
     * NB! This currently THROWS EXCEPTION
     * because using {@link JsonColumn}
     * (that extends {@link org.jskele.libs.dao.JsonValue} and corresponds to `json` data type in Postgres DB)
     * in SQL WHERE clause may produce unexpected results,
     * as Postgres `json` data type (unlike `jsonb`) is sensitive to JSON formatting
     */
    @GenerateSql
    List<TestTableRow> findByJsonColumn(JsonColumn jsonColumn);
}
