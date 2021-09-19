package com.tuan.spring.resolveBeanPostProcesssor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/19 18:14
 */
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName" + beanName + "---执行 postProcessBeforeInitialization 方法");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("beanName" + beanName + "---执行 postProcessAfterInitialization 方法");
		return bean;
	}

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"----执行postProcessBeforeInstantiation方法");
		if (beanClass == BeforeInstantiation.class){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanClass);
            enhancer.setCallback(new MyMethodInterceptor());
            BeforeInstantiation beforeInstantiation = (BeforeInstantiation) enhancer.create();
            System.out.println("创建代理对象："+beforeInstantiation);
			return new BeforeInstantiation();
		}
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"----执行 postProcessAfterInstantiation 方法");

		return false;
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("beanName:"+beanName+"----执行 postProcessProperties 方法");
		return pvs;
	}
}
