package com.javachallenge.challenge.controller;


import com.javachallenge.challenge.dto.AuthDto;
import com.javachallenge.challenge.dto.TokenDto;
import com.javachallenge.challenge.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;

    @PostMapping
	public ResponseEntity<TokenDto> auth(@Valid @RequestBody AuthDto authDto) {
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authDto.getUsername(), authDto.getPassword()));
		return ResponseEntity.ok(new TokenDto(userService.authUser(authentication)));
	}

}