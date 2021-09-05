package com.tuan.spring.custom;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @description: 自定义一个 BeanFactoryPostProcessor，可以针对 beanFactory 修改
 * 在 refresh() -> invokeBeanFactoryPostProcessors() 方法中执行
 *
 * @author: yangtuan
 * @date: 2021/9/4 15:45
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("#########custom beanFactoryPostProcessor.#########");
	}
}
