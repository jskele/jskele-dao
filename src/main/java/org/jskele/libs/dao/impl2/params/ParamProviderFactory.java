package org.jskele.libs.dao.impl2.params;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamProviderFactory {

    private final DataSource dataSource;

    public ParamProvider create(Method method) {
        return ParamProvider.create(dataSource, method);
    }

}
