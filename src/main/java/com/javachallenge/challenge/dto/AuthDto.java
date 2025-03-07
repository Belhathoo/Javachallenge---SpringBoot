package com.javachallenge.challenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {
	
	@NotNull
	private String username;

	@NotNull
	@Size(min=6, max = 10)
	private String password;
}
