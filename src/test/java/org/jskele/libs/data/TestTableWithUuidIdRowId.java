package org.jskele.libs.data;

import org.jskele.libs.values.UuidValue;

import java.util.UUID;

public class TestTableWithUuidIdRowId extends UuidValue {

	public TestTableWithUuidIdRowId() {
		this(UUID.randomUUID());
	}

	public TestTableWithUuidIdRowId(UUID uuid) {
		super(uuid);
	}

	public TestTableWithUuidIdRowId(String string) {
		super(string);
	}

}