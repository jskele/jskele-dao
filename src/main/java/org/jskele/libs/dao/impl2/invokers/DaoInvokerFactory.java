package org.jskele.libs.dao.impl2.invokers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
@RequiredArgsConstructor
public class DaoInvokerFactory {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static List<Function<Method, DaoInvokerConstructor>> mappers = ImmutableList.of(
        DaoInvokerFactory::queryList
    );

   public DaoInvoker createInvoker(Method method) {
       return mappers.stream()
           .map(f -> f.apply(method))
           .filter(Objects::nonNull)
           .findFirst()
           .orElseThrow(()-> new IllegalStateException("DaoInvoker not found for Method " + method))
           .create(jdbcTemplate, method);
   }

    private static DaoInvokerConstructor queryList(Method method) {
       if (true) {
        return QueryListInvoker::new;
       }

       return null;
    }

}
