package com.javachallenge.challenge.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javachallenge.challenge.dto.AuthDto;
import com.javachallenge.challenge.dto.TokenDto;
import com.javachallenge.challenge.dto.UserDto;
import com.javachallenge.challenge.security.jwt.JwtGenerate;
import com.javachallenge.challenge.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtGenerate jwtGenerate;

	@PostMapping
	public ResponseEntity<TokenDto> auth(@Valid @RequestBody AuthDto authDto) {
		String token;
		try {
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authDto.getUsername(), authDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			UserDto userProfile = userService.getUserProfile(authentication.getName());
			token = jwtGenerate.generateToken(authentication, userProfile.getEmail());

		} catch (Exception e) {
            return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(new TokenDto(token));
	}

}