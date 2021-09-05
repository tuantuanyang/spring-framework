package com.tuan.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/5 21:06
 */
@Configuration
@ComponentScan(basePackages = {"com.tuan.spring.annotation"} )
public class ComponentScanConfig {

	@Configuration
	@ComponentScan(basePackages = {"com.tuan.spring.annotation"})
	@Order(6)
	class InnerClass{}
}
