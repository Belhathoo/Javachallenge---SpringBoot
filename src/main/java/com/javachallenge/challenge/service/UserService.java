package com.javachallenge.challenge.service;

import com.javachallenge.challenge.dto.AppUserDto;
import com.javachallenge.challenge.dto.BatchDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {

    String authUser(Authentication authentication);
    AppUserDto getUserProfile(String username);
    AppUserDto getByUsername(String username);

    InputStreamResource generateUserFile(Integer count);

    BatchDto batchUsers(MultipartFile file);
}
