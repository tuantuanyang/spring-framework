package com.tuan.spring.supplier;

import com.tuan.spring.beans.User;

/**
 * @description:
 * @author: yangtuan
 * @date: 2021/9/19 20:29
 */
public class CreateSupplier {

	public static User createUser() {
		return new User("tuan");
	}
}
