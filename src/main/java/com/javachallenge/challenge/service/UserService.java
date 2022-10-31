package com.javachallenge.challenge.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;
import com.javachallenge.challenge.model.User;
import com.javachallenge.challenge.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> UserGenerate() {
		Faker data = new Faker();
		List<User> list = new ArrayList() ;
		System.out.println(data.name().firstName());
		return list;
	}
}
