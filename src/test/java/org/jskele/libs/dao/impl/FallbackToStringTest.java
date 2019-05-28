package org.jskele.libs.dao.impl;

import app.data.TestUuidAsTextDao;
import app.data.TestUuidAsTextRow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FallbackToStringTest {

    @Autowired
    TestUuidAsTextDao testUuidAsTextDao;

    @Test
    public void test_mapping_from_text_to_uuid() {
        List<TestUuidAsTextRow> rows = testUuidAsTextDao.selectAll();
        assertEquals(1, rows.size());
        assertEquals(UUID.fromString("fd838ba4-7555-4ec3-8b48-a37ebe6428b1"), rows.get(0).getUuidText());
    }
}
