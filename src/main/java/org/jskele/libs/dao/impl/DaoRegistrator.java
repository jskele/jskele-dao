package org.jskele.libs.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jskele.libs.dao.Dao;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.Introspector;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Component
@RequiredArgsConstructor
public class DaoRegistrator implements BeanFactoryAware {

    @Value("${jskele.dao.packages:}")
    private String[] packages;
    private BeanFactory beanFactory;
    private final DaoFactory daoFactory;

    @PostConstruct
    public void init() {
        List<String> basePackages = basePackages();

        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;

        basePackages.forEach(appPackage -> new Reflections(appPackage)
                .getTypesAnnotatedWith(Dao.class)
                .forEach(daoClass ->
                        configurableBeanFactory.registerSingleton(
                                beanName(daoClass),
                                daoFactory.create(daoClass)))
        );
    }

    private List<String> basePackages() {
        if (packages == null || packages.length == 0) {
            Object applicationBean = beanFactory.getBean("application");
            return singletonList(substringBeforeLast(applicationBean.getClass().getName(), "."));
        }

        return asList(packages);
    }

    private String beanName(Class<?> daoClass) {
        return Introspector.decapitalize(daoClass.getSimpleName());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
