package com.javachallenge.challenge.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

	private String secretKey;
	private String tokenPrefix;
	private Integer tokenExpirationAfterDays;

	public String getAuthorizationHeader() {
		return HttpHeaders.AUTHORIZATION;
	}

}
