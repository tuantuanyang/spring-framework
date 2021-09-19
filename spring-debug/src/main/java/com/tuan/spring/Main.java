package com.tuan.spring;

import com.tuan.spring.beans.Person;
import com.tuan.spring.beans.User;
import com.tuan.spring.factorybean.MyFactoryBean;
import com.tuan.spring.methodoverride.Apple;
import com.tuan.spring.methodoverride.Banana;
import com.tuan.spring.methodoverride.FruitPlate;
import com.tuan.spring.resolveBeanPostProcesssor.BeforeInstantiation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/8/31 10:40
 */
public class Main {

	public static void main(String[] args) {
//		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//		Person person = context.getBean("person", Person.class);
//		System.out.println(person);

//		ApplicationContext context = new CustomClassPathXmlApplicationContext("applicationContext.xml");
//		// 注解扫描、解析、加载
//		DataSource dataSource = (DataSource) context.getBean("dataSource");
//		System.out.println(dataSource);

//		ApplicationContext context = new ClassPathXmlApplicationContext("converter.xml");
//		context.getBean(User.class);

//		ApplicationContext context = new ClassPathXmlApplicationContext("factoryBean.xml");
//		MyFactoryBean myFactoryBean = (MyFactoryBean) context.getBean("&myFactoryBean");
//		System.out.println(myFactoryBean);
//		User user = (User) context.getBean("myFactoryBean");
//		System.out.println(user);

		/**
		 * spring中默认的对象都是单例的，spring会在一级缓存中持有该对象，方便下次直接获取，
		 * 那么如果是原型作用域的话，会创建一个新的对象
		 * 如果想在一个单例模式的bean下引用一个原型模式的bean,怎么办？
		 * 在此时就需要引用lookup-method标签来解决此问题
		 *
		 * 通过拦截器的方式每次需要的时候都去创建最新的对象，而不会把原型对象缓存起来
		 */
//		ApplicationContext context = new ClassPathXmlApplicationContext("methodoverride.xml");
//		FruitPlate fruitPlate1 = context.getBean("fruitPlate1", FruitPlate.class);
//		Apple apple = (Apple) fruitPlate1.getFruit();
//		System.out.println(apple);
//		FruitPlate fruitPalate2 = context.getBean("fruitPlate2", FruitPlate.class);
//		Banana banana = (Banana) fruitPalate2.getFruit();
//		System.out.println(banana);

//		ApplicationContext ac = new ClassPathXmlApplicationContext("resolveBeforeInstantiation.xml");
//		BeforeInstantiation bean = ac.getBean(BeforeInstantiation.class);
//		bean.doSomeThing();

		ApplicationContext context = new ClassPathXmlApplicationContext("supplier.xml");
		User bean = context.getBean(User.class);
		System.out.println(bean.getName());

	}

}
