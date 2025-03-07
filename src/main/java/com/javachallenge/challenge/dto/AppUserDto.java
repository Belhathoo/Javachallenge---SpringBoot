package com.javachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javachallenge.challenge.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;


@Data
public class AppUserDto {
	private UUID id;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String city;
	private String country;
	private String avatar;
	private String mobile;
	private String jobPosition;
	private String company;
	@NotNull
	private String username;
	@NotNull
	@Email
	private String email;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min=6, max=10)
	private String password;
	@NotNull
	private UserRole role;
}
