package org.jskele.libs.dao;

public interface CrudDao<R extends EntityRow<I>, I> {

	@GenerateSql
	I insert(R row);

	@GenerateSql
	R select(I id);

	@GenerateSql
	@ExcludeNulls
	int update(R row);

	@GenerateSql
	int delete(I id);

}
