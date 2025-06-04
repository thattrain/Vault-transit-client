package com.hashicorp.vaulttransit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(VaultTransitProperties.class)
public class VaultTransitApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaultTransitApplication.class, args);
	}
}