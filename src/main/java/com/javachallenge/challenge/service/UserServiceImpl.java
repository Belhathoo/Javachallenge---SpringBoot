package com.javachallenge.challenge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javachallenge.challenge.dto.AppUserDto;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.exceptions.BadRequestException;
import com.javachallenge.challenge.exceptions.FileException;
import com.javachallenge.challenge.exceptions.UserNotFoundException;
import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.model.UserRole;
import com.javachallenge.challenge.repository.UserRepository;
import com.javachallenge.challenge.security.jwt.JwtGenerate;
import com.javachallenge.challenge.utils.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.Name;
import net.datafaker.fileformats.Format;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerate jwtGenerate;

    @Override
    public String authUser(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDto userProfile = getUserProfile(authentication.getName());
        String token = jwtGenerate.generateToken(authentication, userProfile.getEmail());
        log.info("{} Successfully Authenticated", authentication.getName());
        return token;
    }

    @Override
    public AppUserDto getUserProfile(String username) {
        return repo.findByUsername(username).map(this::entityTDto).orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));
    }

    @Override
    public AppUserDto getByUsername(String username) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (username.equals(authentication.getName())) {
            return getUserProfile(username);
        }

        if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {
            return getUserProfile(username);
        }

        throw new AccessDeniedException("You do not have permission to access this profile.");
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<AppUser> appUser = (new EmailValidator().isValid(username, null))
                ? repo.findByEmail(username)
                : repo.findByUsername(username);

        if (appUser.isEmpty()) {
            log.error("User {} not found!", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name()));

        return new User(appUser.get().getUsername(), appUser.get().getPassword(), authorities);
    }

    public InputStreamResource generateUserFile(Integer count) {
        if (count < 1) {
            throw new BadRequestException("Parameter [count] should be positive");
        }

        String userJson = userGenerate(count);
        try {
            Path path = Files.createTempFile("", ".json");
            Files.write(path, userJson.getBytes());

            File file = path.toFile();
            return new InputStreamResource(new FileInputStream(file));
        } catch (IOException e) {
            throw new FileException(e.getLocalizedMessage());
        }
    }


    @Override
    public String userGenerate(Integer count) {
        Faker dataFaker = new Faker();
        return Format
                .toJson(dataFaker.collection(dataFaker::name).len(count).build())
                .set("firstName", Name::firstName)
                .set("lastName", Name::lastName)
                .set("birthDate", name -> dataFaker.date().birthday().toLocalDateTime().toLocalDate())
                .set("city", name -> dataFaker.address().city())
                .set("country", name -> dataFaker.address().country())
                .set("avatar", name -> dataFaker.avatar().image())
                .set("company", name -> dataFaker.company().name())
                .set("jobPosition", name -> dataFaker.job().position())
                .set("mobile", name -> dataFaker.phoneNumber().cellPhone())
                .set("email", name -> dataFaker.internet().emailAddress())
                .set("username", Name::username)
                .set("password", name -> dataFaker.regexify("[a-zA-Z0-9]{6,10}"))
                .set("role", name -> dataFaker.random().nextBoolean() ? UserRole.ADMIN : UserRole.USER).build().generate();
    }

    @Override
    public BatchDto batchUsers(MultipartFile file) {
        BatchDto resultDto = new BatchDto();

        try {

            List<AppUserDto> users = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {
            });
            resultDto.setTotal(users.size());
            for (AppUserDto userDto : users) {
                try {
                    ValidatorHelper.validate(userDto);
                    AppUser save = dtoTAppUser(userDto);
                    save.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    repo.save(save);
                    resultDto.setImported(resultDto.getImported() + 1);

                } catch (Exception e) {
                    log.error("User: {}  NOT SAVED! \n{}", userDto.getUsername(), e.getMessage());
                    resultDto.setNonImported(resultDto.getNonImported() + 1);
                }

            }
            log.info("{} out of {} user(s) saved", resultDto.getImported(), resultDto.getTotal());
            return resultDto;
        } catch (IOException e) {
            throw new FileException(e.getLocalizedMessage());
        }
    }

    private AppUserDto entityTDto(AppUser user) {
        return objectMapper.convertValue(user, AppUserDto.class);
    }

    private AppUser dtoTAppUser(AppUserDto dto) {
        return objectMapper.convertValue(dto, AppUser.class);
    }

}
