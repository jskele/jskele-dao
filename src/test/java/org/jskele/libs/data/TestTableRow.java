package org.jskele.libs.data;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.EntityRow;

@Getter
@Builder
@RequiredArgsConstructor
public class TestTableRow implements EntityRow<TestTableRowId> {

	private final TestTableRowId id;
	private final String stringColumn;
	private final Integer numericColumn;

}
