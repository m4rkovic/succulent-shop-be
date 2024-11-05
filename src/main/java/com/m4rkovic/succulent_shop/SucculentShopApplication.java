package com.m4rkovic.succulent_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SucculentShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SucculentShopApplication.class, args);
	}

}
