package com.gameup.compra_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.gameup.compra_service.client")
public class CompraServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CompraServiceApplication.class, args);
	}
}