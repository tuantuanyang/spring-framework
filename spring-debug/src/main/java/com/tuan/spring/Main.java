package com.tuan.spring;

import com.tuan.spring.annotation.DataSource;
import com.tuan.spring.config.PropertySourceConfig;
import com.tuan.spring.custom.CustomClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/8/31 10:40
 */
public class Main {

	public static void main(String[] args) {
//		 ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		ApplicationContext context = new CustomClassPathXmlApplicationContext("applicationContext.xml");

		// 注解扫描、解析、加载
		DataSource dataSource = (DataSource) context.getBean("dataSource");
		System.out.println(dataSource);
	}

}
