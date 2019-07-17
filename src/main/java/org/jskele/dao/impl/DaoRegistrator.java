package org.jskele.dao.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jskele.dao.Dao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DaoRegistrator implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(null);

        if (index == null) {
            throw new IllegalStateException("Spring component index not found, please add spring-context-indexer annotation processor");
        }

        Set<String> candidateTypes = index.getCandidateTypes("", Dao.class.getName());

        candidateTypes.stream()
                .map(this::loadClass)
                .forEach(daoClass -> register(daoClass, registry));
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
