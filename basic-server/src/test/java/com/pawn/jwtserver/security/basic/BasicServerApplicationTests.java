package com.pawn.jwtserver.security.basic;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
class BasicServerApplicationTests {

	@Resource
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {

		System.out.println(passwordEncoder.encode("123456"));
	}

	@Test
	public void bCryptPasswordTest(){

		// 每次加密都不一样 生成60位字符串
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		String password = "123456";
		String encodePassword = passwordEncoder.encode(password);

		System.out.println("原始密码" + password);
		System.out.println("加密过后的密码" + encodePassword);
		System.out.println("原始密码是否与加密过后的密码是否匹配：" +
				(passwordEncoder.matches(password, encodePassword) ? "是":"否"));
		System.out.println("密码是否匹配：" +
				(passwordEncoder.matches("654321", encodePassword) ? "是":"否"));
	}

}
