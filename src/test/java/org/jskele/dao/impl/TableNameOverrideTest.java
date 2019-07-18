package org.jskele.dao.impl;

import app.data.TestTable2Dao;
import app.data.TestTableRow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TableNameOverrideTest {

    @Autowired
    TestTable2Dao dao;


    @Test
    public void shouldQueryAllRows() {
        // When
        List<TestTableRow> result = dao.selectAll();

        // Then
        assertThat(result, hasSize(greaterThan(0)));
    }

}
