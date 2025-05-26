package com.afrosofttech.rest_jwt_demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info=@Info(
                title = "JWT Demo API",
                version = "Versions 1.0",
                contact = @Contact(
                        name = "afrosofttech", email="amadou.asj.jallow@gmaill.com", url = "https://afrosofttech.com"
                ),
                description = "API for demonstrating integration of JWT with Spring Boot",
                termsOfService = "https://afrosofttech.com/terms",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        )
)
public class SwaggerConfig {
}
