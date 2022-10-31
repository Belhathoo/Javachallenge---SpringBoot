package com.javachallenge.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javachallenge.challenge.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
}
