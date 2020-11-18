package com.pawn.courses;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.pawn.courses"})
public class BasicServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicServerApplication.class, args);
	}

}
