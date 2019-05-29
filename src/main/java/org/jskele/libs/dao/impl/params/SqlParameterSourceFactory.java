package org.jskele.libs.dao.impl.params;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SqlParameterSourceFactory {

    private final DataSource dataSource;

    public SqlParameterSource[] createArray(ParameterExtractor extractor, Object[] args) {
        Collection<?> collection = (Collection<?>) args[0];

        return collection.stream()
                .map(arg -> create(extractor, new Object[]{arg}))
                .toArray(SqlParameterSource[]::new);
    }

    public SqlParameterSource create(ParameterExtractor extractor, Object[] args) {
        DaoSqlParameterSource daoSqlParameterSource = new DaoSqlParameterSource(dataSource);

        String[] names = extractor.names();
        Object[] values = extractor.values(args);

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object value = values[i];

            daoSqlParameterSource.addValue(name, value);
        }

        return daoSqlParameterSource;
    }
}
