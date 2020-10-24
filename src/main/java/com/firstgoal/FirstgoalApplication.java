package com.firstgoal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FirstgoalApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstgoalApplication.class, args);
	}

}
