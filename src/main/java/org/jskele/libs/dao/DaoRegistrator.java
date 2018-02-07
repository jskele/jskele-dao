package org.jskele.libs.dao;

import java.beans.Introspector;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DaoRegistrator implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		String appPackage = basePackage(registry);

		new Reflections(appPackage)
				.getSubTypesOf(Dao.class)
				.forEach(daoClass -> registry.registerBeanDefinition(
						beanName(daoClass),
						beanDefinition(daoClass)));
	}

	private String basePackage(BeanDefinitionRegistry registry) {
		// TODO: base package from property, fallback to application bean
		BeanDefinition appDef = registry.getBeanDefinition("application");
		return StringUtils.substringBeforeLast(appDef.getBeanClassName(), ".");
	}

	private String beanName(Class<? extends Dao> daoClass) {
		return Introspector.decapitalize(daoClass.getSimpleName());
	}

	private BeanDefinition beanDefinition(Class<? extends Dao> daoClass) {
		BeanDefinition bd = new GenericBeanDefinition();

		bd.setFactoryBeanName(DaoFactory.BEAN_NAME);
		bd.setFactoryMethodName(DaoFactory.METHOD_NAME);
		bd.getConstructorArgumentValues().addIndexedArgumentValue(0, daoClass);

		return bd;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// not used
	}

}