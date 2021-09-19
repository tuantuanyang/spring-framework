package com.tuan.spring.factorybean;

import com.tuan.spring.beans.User;
import org.springframework.beans.factory.FactoryBean;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/14 22:07
 */
public class MyFactoryBean implements FactoryBean<User> {
	@Override
	public User getObject() throws Exception {
		return new User("tom");
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
