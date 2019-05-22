package org.jskele.libs.dao.impl;

import com.google.common.reflect.Reflection;
import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.invokers.DaoInvokerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DaoFactory {

    static final String BEAN_NAME = "daoFactory";
    static final String METHOD_NAME = "create";

    private final DaoInvokerFactory invokerFactory;

    public <T> T create(Class<T> daoClass) {
        DaoInvocationHandler handler = new DaoInvocationHandler(invokerFactory, daoClass);
        return Reflection.newProxy(daoClass, handler);
    }
}
