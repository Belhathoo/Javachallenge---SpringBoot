package com.javachallenge.challenge.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javachallenge.challenge.dto.AppUserDto;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {

	private final UserService userService;

	@GetMapping("/generate")
	ResponseEntity<InputStreamResource> generateUsers(@RequestParam("count") Integer count) {

		log.info("Generating {} Users", count);
		try {
			Path path = Files.createTempFile("", ".json");
			Files.write(path, userService.userGenerate(count).getBytes());
			File file = path.toFile();
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.header("Content-Disposition", "attachment; filename=\"" + path.getFileName() + "\"")
					.contentLength(file.length())
					.contentType(MediaType.APPLICATION_JSON)
					.body(resource);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping(path = "/batch", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	ResponseEntity<BatchDto> batch(@RequestPart MultipartFile file) throws Exception{
		return ResponseEntity.ok(userService.batchUsers(file));
	}

	@SecurityRequirement(name = "challengeapi")
	@GetMapping("/me")
	ResponseEntity<AppUserDto> getMyProfile() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return ResponseEntity.accepted().body(userService.getUserProfile(authentication.getName()));
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@SecurityRequirement(name = "challengeapi")
	@GetMapping("/{username}")
	ResponseEntity<AppUserDto> getUserProfile(@PathVariable("username") final String username) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (username.equals(authentication.getName()))
			return ResponseEntity.accepted().body(userService.getUserProfile(authentication.getName()));
		if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")))
			return ResponseEntity.accepted().body(userService.getUserProfile(username));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();


	}

}
