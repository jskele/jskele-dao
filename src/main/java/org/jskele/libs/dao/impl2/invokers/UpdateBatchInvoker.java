package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UpdateBatchInvoker implements DaoInvoker {

    @Override
    public boolean accepts(Method method) {
        return false;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
        return null;
    }
}
