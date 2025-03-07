package com.javachallenge.challenge.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor
@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

	private String secretKey;
	private String tokenPrefix;
	private Integer tokenExpirationHours;

	public String getAuthorizationHeader() {
		return HttpHeaders.AUTHORIZATION;
	}

}
