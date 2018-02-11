package org.jskele.libs.dao.impl2.sql2;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;

import org.jskele.libs.dao.SqlTemplate;
import org.jskele.libs.dao.impl2.params2.ParameterExtractor;

import com.google.common.io.Resources;
import com.google.common.reflect.Reflection;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@RequiredArgsConstructor
class ClasspathSqlLoader {
    private static final PebbleEngine TEMPLATE_ENGINE = new PebbleEngine
        .Builder()
        .loader(new StringLoader())
        .autoEscaping(false)
        .build();


    private final Method method;
    private final ParameterExtractor extractor;

    public SqlSource createSource() {
        if (isTemplated()) {
            return createDynamicSource();
        }

        return createStaticSource();
    }

    private SqlSource createStaticSource() {
        String sqlString = loadAsString("sql");

        return args -> sqlString;
    }

    private SqlSource createDynamicSource() {
        String templateString = loadAsString("peb");
        PebbleTemplate template;
        try {
            template = TEMPLATE_ENGINE.getTemplate(templateString);
        } catch (PebbleException e) {
            throw new RuntimeException(e);
        }

        return args -> {
            String[] names = extractor.names();
            Object[] values = extractor.values(args);

            Map<String, Object> context = IntStream.range(0, names.length).boxed()
                .collect(toMap(
                    i -> names[i],
                    i -> values[i]
                ));

            StringWriter writer = new StringWriter();
            try {
                template.evaluate(writer, context);
            } catch (PebbleException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            return writer.toString();
        };
    }

    public String loadAsString(String suffix) {
        try {
            return Resources.toString(resourceUrl(suffix), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL resourceUrl(String suffix) {
        Class<?> declaringClass = method.getDeclaringClass();

        String methodName = method.getName();
        String packageName = Reflection.getPackageName(declaringClass);
        String fileName = "/" + packageName.replace(".", "/") + "/" + methodName + "." + suffix;

        return declaringClass.getResource(fileName);
    }

    public boolean isTemplated() {
        return method.getAnnotation(SqlTemplate.class) != null;
    }
}
