package com.example.UniLabPass;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UniLabPassApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniLabPassApplication.class, args);
	}

}
