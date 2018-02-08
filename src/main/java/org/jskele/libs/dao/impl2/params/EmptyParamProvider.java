package org.jskele.libs.dao.impl2.params;

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

class EmptyParamProvider extends ParamSourceProvider {

    @Override
    public SqlParameterSource getParams(Object[] args) {
        return new EmptySqlParameterSource();
    }

}
