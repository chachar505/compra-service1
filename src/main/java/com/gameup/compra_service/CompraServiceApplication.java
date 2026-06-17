package com.gameup.compra_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gameup.compra_service.client")
public class CompraServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CompraServiceApplication.class, args);
	}
}