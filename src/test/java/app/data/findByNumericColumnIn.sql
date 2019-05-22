SELECT *
FROM test_table
where numeric_column = ANY (:numericColumns)
  and string_column != :excludedValue