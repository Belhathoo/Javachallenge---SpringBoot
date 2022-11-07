package com.javachallenge.challenge.security.jwt;


import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// @NoArgsConstructor
// @Getter @Setter
// @ConfigurationProperties(prefix = "application.jwt")

// public class JwtConfig {

//     private String secretKey;
//     private String tokenPrefix;
//     private Integer tokenExpirationAfterDays;


//     public String getAuthorizationHeader() {
//         return HttpHeaders.AUTHORIZATION;
//     }
	

@Setter
@Getter
@NoArgsConstructor
@Configuration
public class JwtConfig {

    @Value("${application.jwt.secretKey}")
    private String secretKey;
    @Value("${application.jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("${application.jwt.tokenExpirationAfterDays}")
    private Integer tokenExpirationAfterDays;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(this.secretKey.getBytes());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
