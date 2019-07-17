package app.data;

import org.jskele.dao.Dao;
import org.jskele.dao.GenerateSql;

import java.util.List;

@Dao
public interface TestUuidAsTextDao {

    @GenerateSql
    List<TestUuidAsTextRow> selectAll();
}
