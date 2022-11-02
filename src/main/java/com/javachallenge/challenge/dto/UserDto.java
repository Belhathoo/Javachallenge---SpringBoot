package com.javachallenge.challenge.dto;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.javachallenge.challenge.model.UserRole;

import lombok.Data;

@Data
public class UserDto {
	private UUID id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private String city;
	private String country;
	private String avatar;
	private String mobile;
	private String jobPosition;
	private String company;
	@NotNull
	private String username;
	@NotNull
	private String email;
	@Size(min=6, max=10)
	private String password;
	@NotNull
	private UserRole role;
}
