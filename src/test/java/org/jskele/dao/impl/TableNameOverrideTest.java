package org.jskele.dao.impl;

import app.data.TestTable2Dao;
import app.data.TestTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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
