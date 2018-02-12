package org.jskele.libs.dao.impl2;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.Dao;
import org.jskele.libs.dao.impl2.invokers.DaoInvokerFactory;
import org.springframework.stereotype.Component;

import com.google.common.reflect.Reflection;

@Component
@RequiredArgsConstructor
class DaoFactory {

    private final DaoInvokerFactory invokerFactory;

    public <T extends Dao> T create(Class<T> daoClass) {
        DaoInvocationHandler handler = new DaoInvocationHandler(invokerFactory);
        return Reflection.newProxy(daoClass, handler);
    }
}
