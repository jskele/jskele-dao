package app.data;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.GenerateSql;

import java.util.List;

@Dao
public interface TestUuidAsTextDao {

    @GenerateSql
    List<TestUuidAsTextRow> selectAll();
}
