package app.data;

import org.jskele.dao.Dao;
import org.jskele.dao.GenerateSql;

import java.util.List;

@Dao(table = "test_table")
public interface TestTable2Dao {

    @GenerateSql
    List<TestTableRow> selectAll();
}
