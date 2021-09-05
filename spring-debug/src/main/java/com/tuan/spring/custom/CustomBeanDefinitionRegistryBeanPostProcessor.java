package com.tuan.spring.custom;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @description: 自定义实现 {@link BeanDefinitionRegistryPostProcessor},也是 {@link BeanFactoryPostProcessor} 的子接口
 *
 * 都是在 refresh() -> invokeBeanFactoryPostProcessors() 方法中执行
 *
 * @author: yangtuan
 * @date: 2021/9/5 20:59
 */
public class CustomBeanDefinitionRegistryBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
