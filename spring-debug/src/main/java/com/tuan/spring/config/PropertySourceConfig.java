package com.tuan.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/5 21:12
 */
@Configuration
@PropertySource({"classpath:db-mysql.properties"})
public class PropertySourceConfig {

}
