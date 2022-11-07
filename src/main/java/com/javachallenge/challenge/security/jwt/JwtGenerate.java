package com.javachallenge.challenge.security.jwt;

import java.time.LocalDate;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtGenerate {

	private final JwtConfig jwtConfig;
	private final SecretKey secretKey;

	public String generateToken(Authentication authentication, String email) {
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim("authorities", authentication.getAuthorities())
				.claim("email", email)
				.setIssuedAt(new Date())
				.setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
				.signWith(secretKey)
				.compact();
	}

}
