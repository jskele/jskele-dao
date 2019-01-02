package org.jskele.libs.dao.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.jskele.libs.data.TestTableDao;
import org.jskele.libs.data.TestTableRow;
import org.jskele.libs.data.TestTableRowId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoInvocationHandlerTest {

	@Autowired
	private TestTableDao dao;

	@Test
	public void shouldInsertRow() {
		// Given
		TestTableRow row = TestTableRow.builder()
				.stringColumn("value")
				.numericColumn(99)
				.build();

		// When
		TestTableRowId insertedId = dao.insert(row);
		TestTableRow actual = dao.select(insertedId);

		// Then
		assertThat(insertedId.toValue(), equalTo(4L));
		assertThat(actual.getNumericColumn(), equalTo(99));
		assertThat(actual.getStringColumn(), equalTo("value"));
	}

	@Test
	public void shouldInsertBatch() {
		// Given
		TestTableRow row1 = createRow(8L, "batch1", 8);
		TestTableRow row2 = createRow(9L, "batch2", 9);

		// When
		dao.insertBatch(newArrayList(row1, row2));
		List<TestTableRow> actualRows = dao.findByNumericColumnIn("excludedValue", newArrayList(8L, 9L));

		// Then
		assertThat(actualRows, hasSize(2));
	}

	@Test
	public void shouldBatchUpdate() {
		// Given
		TestTableRow row1 = createRow(1L, "row1", 11);
		TestTableRow row2 = createRow(3L, "batchUpdate3", 33);

		// When
		int[] updatedCounts = dao.updateBatch(newArrayList(row1, row2));
		List<TestTableRow> actualRows = dao.findByNumericColumnIn("excludedValue", newArrayList(11L, 33L));

		// Then
		assertThat(updatedCounts, equalTo(new int[]{1,1}));
		assertThat(actualRows, hasSize(2));
	}

	@Test
	public void shouldUpdateRow() {
		// Given
		long idToBeUpdated = 1L;
		TestTableRow rowToBeUpdated = TestTableRow.builder()
				.id(new TestTableRowId(idToBeUpdated))
				.numericColumn(10)
				.build();
		TestTableRow expected = TestTableRow.builder()
				.id(new TestTableRowId(idToBeUpdated))
				.stringColumn("row1")
				.numericColumn(10)
				.build();

		// When
		int rowsUpdatedCount = dao.update(rowToBeUpdated);
		TestTableRow actual = dao.select(new TestTableRowId(idToBeUpdated));

		// Then
		assertThat(rowsUpdatedCount, equalTo(1));
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldDeleteRow() {
		// Given
		long id = 2L;

		// When
		int rowsAffected = dao.delete(new TestTableRowId(id));

		// Then
		assertThat(rowsAffected, equalTo(1));
	}

	@Test
	public void shouldQueryAllRows() {
		// When
		List<TestTableRow> result = dao.selectAll();

		// Then
		assertThat(result, hasSize(greaterThan(0)));
	}

	private TestTableRow createRow(Long id, String value, int numeric) {
		return TestTableRow.builder()
				.id(new TestTableRowId(id))
				.stringColumn(value)
				.numericColumn(numeric)
				.build();
	}
}
