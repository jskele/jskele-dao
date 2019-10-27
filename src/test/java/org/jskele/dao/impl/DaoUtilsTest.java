package org.jskele.dao.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DaoUtilsTest {


    @Getter
    @RequiredArgsConstructor
    public static class TestRow {
        private final String test;
    }

    @Test
    public void test() {
        String[] strings = DaoUtils.beanProperties(TestRow.class);
        assertTrue(strings.length == 1);
    }

}
