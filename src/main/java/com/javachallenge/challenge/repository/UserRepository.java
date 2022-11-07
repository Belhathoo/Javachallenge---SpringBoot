package com.javachallenge.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javachallenge.challenge.model.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

	// AppUser findByUsername(String username);
	Optional<AppUser> findByUsername(String username);
	Optional<AppUser> findByEmail(String email);
}
