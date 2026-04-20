package edu.bi.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringdemoApplication {

	public static void main(String[] args) {

		
		System.out.println(new BCryptPasswordEncoder().encode("admin123"));

		SpringApplication.run(SpringdemoApplication.class, args);
	}
}