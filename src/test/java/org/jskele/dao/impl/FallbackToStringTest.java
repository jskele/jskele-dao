package org.jskele.dao.impl;

import app.data.TestUuidAsTextDao;
import app.data.TestUuidAsTextRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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
