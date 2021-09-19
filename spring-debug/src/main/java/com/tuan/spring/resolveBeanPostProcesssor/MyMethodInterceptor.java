package com.tuan.spring.resolveBeanPostProcesssor;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/19 18:12
 */
public class MyMethodInterceptor implements MethodInterceptor {
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("目标方法执行之前：" + method);
		Object o1 = methodProxy.invokeSuper(o, objects);
		System.out.println("目标方法执行之后：" + method);
		return o1;
	}
}
