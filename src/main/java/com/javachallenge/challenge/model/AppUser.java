package com.javachallenge.challenge.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "AppUser")
@RequiredArgsConstructor
public class AppUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private String firstName;
	private String lastName;
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	private String city;
	private String country;
	private String mobile;
	private String avatar;
	private String jobPosition;
	private String company;
	@Column(nullable=false, unique=true)
	private String username;
	@Column(nullable=false, unique=true)
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserRole role;

	public AppUser(String username, String pswd, String email, UserRole userRole) {
		this.username = username;
		this.password = pswd;
		this.email = email;
		this.role = userRole;
	}
}