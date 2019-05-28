CREATE TABLE test_table
(
  id             BIGSERIAL PRIMARY KEY,
  json_column    json,
  string_column  TEXT,
  numeric_column INTEGER
);

CREATE TABLE test_table_with_uuid_id
(
  id             UUID PRIMARY KEY,
  string_column  TEXT,
  numeric_column INTEGER
);

CREATE TABLE test_uuid_as_text
(
    uuid_text TEXT PRIMARY KEY
)
