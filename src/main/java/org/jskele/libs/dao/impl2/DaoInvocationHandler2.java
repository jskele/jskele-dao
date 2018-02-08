package org.jskele.libs.dao.impl2;

import java.lang.reflect.Method;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.impl2.invokers.DaoInvoker;

import com.google.common.reflect.AbstractInvocationHandler;

@RequiredArgsConstructor
class DaoInvocationHandler2 extends AbstractInvocationHandler {

    private final List<DaoInvoker> invokers;

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) {
        DaoInvoker invoker = findInvoker(method);
        return invoker.invoke(method, args);
    }

    private DaoInvoker findInvoker(Method method){
        return invokers.stream()
            .filter(invoker -> invoker.accepts(method))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("No matching invoker found for method " + method));
    }

}
