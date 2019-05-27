package org.jskele.libs.dao.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

public class DaoUtilsTest {

	@Getter
	@RequiredArgsConstructor
	public static class TestRow {

		private final String test;

	}

	@Test
	public void test() {
		String[] strings = DaoUtils.beanProperties(TestRow.class);
		Assert.assertTrue(strings.length == 1);
	}

}
