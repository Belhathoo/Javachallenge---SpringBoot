package com.javachallenge.challenge.dto;

import java.sql.Date;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.javachallenge.challenge.model.UserRole;

import lombok.Data;


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
