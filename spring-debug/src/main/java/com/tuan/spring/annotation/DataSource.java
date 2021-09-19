package com.tuan.spring.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/5 21:27
 */
@Component
public class DataSource {
	@Value("${jdbc.username}")
	private String userName;

	@Value("${jdbc.password}")
	private String passWord;

	@Value("${jdbc.driver}")
	private String driver;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "DataSource{" +
				"userName='" + userName + '\'' +
				", passWord='" + passWord + '\'' +
				", driver='" + driver + '\'' +
				'}';
	}
}
