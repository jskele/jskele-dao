package org.jskele.libs.data;

import org.jskele.libs.dao.CrudDao;
import org.jskele.libs.dao.GenerateSql;

import java.util.List;

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
}
