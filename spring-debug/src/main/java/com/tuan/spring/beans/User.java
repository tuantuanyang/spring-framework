package com.tuan.spring.beans;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/14 22:09
 */
public class User {
	private String name;

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
