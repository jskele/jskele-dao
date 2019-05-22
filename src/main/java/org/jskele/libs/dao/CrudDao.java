package org.jskele.libs.dao;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

public interface CrudDao<R extends EntityRow<I>, I> {

    @GenerateSql
    I insert(R row);

    /**
     * @param rows - non-empty list of rows to be inserted
     * @return an array containing the numbers of rows affected by each insert operation in the batch
     * @see - internally it uses
     * {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations#batchUpdate(String, SqlParameterSource[])}
     */
    @GenerateSql
    int[] insertBatch(List<R> rows);

    @GenerateSql
    boolean exists(I id);

    @GenerateSql
    R select(I id);

    @GenerateSql
    @ExcludeNulls
    int update(R row);

    @GenerateSql
    int delete(I id);

}
