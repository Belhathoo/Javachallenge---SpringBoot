package com.javachallenge.challenge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javachallenge.challenge.model.User;
import com.javachallenge.challenge.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UsersGenerate {
	private final UserService userService;

	public UsersGenerate(UserService userService) {
		this.userService = userService;
		
	}
	@GetMapping("/generate")
	ResponseEntity<List<User>> generateUsers(@RequestParam int count){
		List<User> users;
		return new ResponseEntity<>(userService.UserGenerate(), HttpStatus.OK);
	}
}
