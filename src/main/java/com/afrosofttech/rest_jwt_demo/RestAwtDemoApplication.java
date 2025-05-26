package com.afrosofttech.rest_jwt_demo;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "afrosofttech-demo-api",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER)
public class RestAwtDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestAwtDemoApplication.class, args);
	}

}
