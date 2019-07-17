package org.jskele.dao.impl.sql;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.common.reflect.Reflection;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import org.jskele.dao.SqlTemplate;
import org.jskele.dao.impl.DaoUtils;
import org.jskele.dao.impl.params.ParameterExtractor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.jskele.dao.impl.DaoUtils.hasAnnotation;

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
            Map<String, Object> context = DaoUtils.getParamValuesByName(args, extractor);

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

        URL resource = declaringClass.getResource(fileName);
        Preconditions.checkState(resource != null, "Resource " + fileName + " doesn't exist in runtime classpath. " +
                "If you want to keep sql files next to Dao interfaces instead of resources folder, then configure build tool to copy sql files to runtime classpath!");
        return resource;
    }

    public boolean isTemplated() {
        return hasAnnotation(method, SqlTemplate.class);
    }
}
