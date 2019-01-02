package org.jskele.libs.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jskele.libs.dao.EntityRow;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTableRow implements EntityRow<TestTableRowId> {

	private final TestTableRowId id;
	private final String stringColumn;
	private final Integer numericColumn;

}
