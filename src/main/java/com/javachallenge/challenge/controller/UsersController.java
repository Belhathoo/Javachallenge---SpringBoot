package com.javachallenge.challenge.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {
	private final UserService userService;

@GetMapping("/allusers")
ResponseEntity<List<AppUser>> getAllUsers() {
	return ResponseEntity.ok(userService.getAllUsers());

}

	@GetMapping("/generate")
	ResponseEntity<InputStreamResource> generateUsers(@RequestParam("count") Integer count) {
		// EXception count

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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping(path="/batch", consumes= { MediaType.MULTIPART_FORM_DATA_VALUE})
	ResponseEntity<BatchDto> batch(@RequestPart MultipartFile file) {
		return ResponseEntity.ok(userService.batchUsers(file));
	}

}
