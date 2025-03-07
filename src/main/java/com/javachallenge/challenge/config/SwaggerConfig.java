package com.javachallenge.challenge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(info = @Info(title = "Java Challenge API", version = "v1.1.0", description = "JavaChallenge API documentation"))
@SecurityScheme(name = "challengeapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@Configuration
public class SwaggerConfig {
}
