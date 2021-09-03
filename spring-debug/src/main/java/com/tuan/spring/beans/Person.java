package com.tuan.spring.beans;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/8/31 10:44
 */
public class Person {
	private int id;

	private String name;

	public Person() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
