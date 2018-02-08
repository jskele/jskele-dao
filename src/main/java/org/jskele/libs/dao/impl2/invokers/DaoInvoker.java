package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;

public interface DaoInvoker {

    boolean accepts(Method method);

    Object invoke(Method method, Object[] args);

}
