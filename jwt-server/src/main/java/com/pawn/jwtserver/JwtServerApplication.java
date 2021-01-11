package com.pawn.jwtserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.pawn.jwtserver"})
public class JwtServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtServerApplication.class, args);
	}

}
