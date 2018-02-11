package org.jskele.libs.dao.impl2.params2;

public interface ParameterExtractor {

    String[] names();

    Object[] values(Object[] args);

}
