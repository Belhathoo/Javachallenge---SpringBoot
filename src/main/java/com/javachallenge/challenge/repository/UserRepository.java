package com.javachallenge.challenge.repository;

import com.javachallenge.challenge.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

	// AppUser findByUsername(String username);
	Optional<AppUser> findByUsername(String username);
	Optional<AppUser> findByEmail(String email);
}
