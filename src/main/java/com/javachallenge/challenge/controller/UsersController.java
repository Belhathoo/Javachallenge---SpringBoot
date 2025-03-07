package com.javachallenge.challenge.controller;

import com.javachallenge.challenge.dto.AppUserDto;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    @GetMapping("/generate")
    ResponseEntity<InputStreamResource> generateUsers(@RequestParam("count") Integer count) throws IOException {

        InputStreamResource resource = userService.generateUserFile(count);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Disposition", "attachment; filename= users.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    @PostMapping(path = "/batch", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<BatchDto> batch(@RequestPart MultipartFile file) {
        return ResponseEntity.ok(userService.batchUsers(file));
    }

    @SecurityRequirement(name = "challengeapi")
    @GetMapping("/me")
    ResponseEntity<AppUserDto> getMyProfile() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.ok().body(userService.getUserProfile(authentication.getName()));
    }

    @SecurityRequirement(name = "challengeapi")
    @GetMapping("/{username}")
    ResponseEntity<AppUserDto> getUserProfile(@PathVariable("username") final String username) {
        return ResponseEntity.ok().body(userService.getByUsername(username));
    }

}
