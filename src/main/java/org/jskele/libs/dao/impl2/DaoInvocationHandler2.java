package org.jskele.libs.dao.impl2;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.invokers.DaoInvoker;
import org.jskele.libs.dao.impl2.invokers.DaoInvokerFactory;

import com.google.common.reflect.AbstractInvocationHandler;

@RequiredArgsConstructor
class DaoInvocationHandler2 extends AbstractInvocationHandler {

    private final DaoInvokerFactory invokerFactory;

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) {
        DaoInvoker invoker = invokerFactory.create(method);

        // TODO: add cache (Meelis Lehtmets, 2018-02-08)

        return invoker.invoke(args);
    }

}
