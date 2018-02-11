package org.jskele.libs.dao.impl2.sql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public interface SqlProvider {

    String createSql(SqlParameterSource parameterSource);

}
