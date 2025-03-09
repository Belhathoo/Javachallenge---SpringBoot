package com.javachallenge.challenge.security.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtGenerate {

	private final JwtConfig jwtConfig;
	private final SecretKey secretKey;

	public String generateToken(Authentication authentication, String email) {
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim("role", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.claim("email", email)
				.setIssuedAt(new Date())
				.setExpiration(Date.from(LocalDateTime.now()
						.plusHours(jwtConfig.getTokenExpirationHours()).atZone(ZoneId.systemDefault())
						.toInstant()))
				.signWith(secretKey)
				.compact();
	}

	public String generateExpiredToken(String username, String email) {
		Date pastDate = new Date(System.currentTimeMillis() - 3600 * 1000); // 1 hour ago
		return Jwts.builder()
				.setSubject(username)
				.claim("email", email)
				.setIssuedAt(new Date())
				.setExpiration(pastDate)
				.signWith(secretKey)
				.compact();
	}

}
