package org.jskele.libs.dao.impl;

import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.impl.invokers.DaoInvoker;
import org.jskele.libs.dao.impl.invokers.DaoInvokerFactory;

import java.lang.reflect.Method;
import java.util.Map;

@RequiredArgsConstructor
class DaoInvocationHandler extends AbstractInvocationHandler {

    private final DaoInvokerFactory invokerFactory;
    private final Map<Method, DaoInvoker> invokerMap = Maps.newConcurrentMap();
    private final Class<?> daoClass;

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) {
        DaoInvoker invoker = invokerMap.computeIfAbsent(
                method,
                m -> invokerFactory.create(m, daoClass)
        );

        return invoker.invoke(args);
    }

}
