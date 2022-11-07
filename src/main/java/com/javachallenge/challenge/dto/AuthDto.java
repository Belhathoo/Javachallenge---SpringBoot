package com.javachallenge.challenge.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AuthDto {
	
	@NotNull
	private String username;

	@NotNull
	@Size(min=6, max = 10)
	private String password;
}
