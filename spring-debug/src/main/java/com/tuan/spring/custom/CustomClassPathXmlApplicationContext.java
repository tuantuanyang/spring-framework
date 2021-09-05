package com.tuan.spring.custom;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/4 12:53
 */
public class CustomClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

	public CustomClassPathXmlApplicationContext(String configLocation) {
		super(configLocation);
	}

	/**
	 * refresh() -> prepareRefresh() ->  自定义初始化资源
	 */
	@Override
	protected void initPropertySources() {
		System.out.println("Expend implements initPropertySource");
	}

	/**
	 * refresh() -> obtainFreshBeanFactory() -> refreshBeanFactory() ->
	 * 可以直接拿到 beanFactory 对象，然后自定义修改
	 * @param beanFactory the newly created bean factory for this context
	 */
	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		super.addBeanFactoryPostProcessor(new CustomBeanFactoryPostProcessor());
	}

	/**
	 * refresh() -> 也是可以针对 beanFactory 拓展，但是一般自己很少修改，可以看看 web 中是怎么拓展的
	 * @param beanFactory the bean factory used by the application context
	 */
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		System.out.println("Expend implements postProcessBeanFactory.");
	}
}
