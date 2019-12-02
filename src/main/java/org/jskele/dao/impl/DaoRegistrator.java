package org.jskele.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jskele.dao.Dao;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class DaoRegistrator implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware {
    public static final String JSKELE_DAO_PACKAGES_BEAN_NAME = "jskeleDaoPackages";

    @Setter
    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        Stream<? extends Class<?>> daoBeanCandidateInterfaces = findDaoBeanCandidateInterfaces();

        daoBeanCandidateInterfaces
                .forEach(daoClass -> register(daoClass, registry));
    }

    Stream<Class<?>> findDaoBeanCandidateInterfaces() {
        boolean springComponentsIndexIgnored = SpringProperties.getFlag(CandidateComponentsIndexLoader.IGNORE_INDEX);
        if (springComponentsIndexIgnored) {
            String[] basePackages = basePackages();
            return findDaoBeanCandidatesInterfacesFromPackages(basePackages);
        }
        return findDaoBeanCandidateInterfacesFromSpringComponentsIndex();
    }

    private Stream<Class<?>> findDaoBeanCandidateInterfacesFromSpringComponentsIndex() {
        CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(null);
        if (index == null) {
            throw new IllegalStateException("Spring component index not found, please add spring-context-indexer annotation processor");
        }

        return index.getCandidateTypes("", Dao.class.getName()).stream()
                .map(this::loadClass);
    }

    private Stream<Class<?>> findDaoBeanCandidatesInterfacesFromPackages(String[] basePackages) {
        return Stream.of(basePackages)
                .map(this::findDaoClassesFromPackage)
                .flatMap(Collection::stream);
    }

    private Set<Class<?>> findDaoClassesFromPackage(String appPackage) {
        return new Reflections(appPackage).getTypesAnnotatedWith(Dao.class);
    }

    private String[] basePackages() {
        try {
            return beanFactory.getBean(JSKELE_DAO_PACKAGES_BEAN_NAME, String[].class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("As using Spring components index is disabled, " +
                    "you must provide packages of `@Dao` interfaces for classpath scanning with bean named " +
                    "(using bean with name " + JSKELE_DAO_PACKAGES_BEAN_NAME + ") that returns `String[]`, " +
                    "so Spring beans could be created for `@Dao` interfaces."
            );
        }
    }

    private void register(Class<?> daoClass, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(
                beanName(daoClass),
                beanDefinition(daoClass)
        );
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
    }


    private Class<?> loadClass(String s) {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String beanName(Class<?> daoClass) {
        Component componentAnnotation = daoClass.getAnnotation(Component.class);
        if (componentAnnotation != null && StringUtils.isNotBlank(componentAnnotation.value())) {
            return componentAnnotation.value();
        }
        return Introspector.decapitalize(daoClass.getSimpleName());
    }

    private BeanDefinition beanDefinition(Class<?> daoClass) {
        BeanDefinition bd = new GenericBeanDefinition();

        bd.setFactoryBeanName(DaoFactory.BEAN_NAME);
        bd.setFactoryMethodName(DaoFactory.METHOD_NAME);
        bd.getConstructorArgumentValues().addIndexedArgumentValue(0, daoClass);

        return bd;
    }
}
