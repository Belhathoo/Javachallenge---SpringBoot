package com.javachallenge.challenge.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.dto.UserDto;
import com.javachallenge.challenge.exceptions.NotFoundException;
import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.model.UserRole;
import com.javachallenge.challenge.repository.UserRepository;
import com.javachallenge.challenge.utils.ValidatorHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.App;
import net.datafaker.Faker;
import net.datafaker.Name;
import net.datafaker.fileformats.Format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository repo;
	private final ObjectMapper objectMapper;

	public UserDetails loadUserByUsername(String username) throws NotFoundException {
		Optional<AppUser> appUser = (new EmailValidator().isValid(username, null))
				? repo.findByEmail(username)
				: repo.findByUsername(username);

		if (appUser.isEmpty()) {
			log.error("User " + username + " not found");
			throw new NotFoundException("User " + username + " not found");
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name()));

		return new User(
				appUser.get().getUsername(),
				appUser.get().getPassword(),
				authorities);
	}

	public String userGenerate(Integer count) {
		Faker dataFaker = new Faker();
		final String json = Format.toJson(
				dataFaker.collection(dataFaker::name)
						.len(count)
						.build())
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
				.set("role", name -> dataFaker.random().nextBoolean() ? UserRole.ADMIN : UserRole.USER)
				.build().generate();
		return json;
	}

	public BatchDto batchUsers(MultipartFile file) {
		BatchDto resultDto = new BatchDto();

		try {
			List<UserDto> users = objectMapper
					.readValue(file.getInputStream(), new TypeReference<List<UserDto>>() {
					});
			resultDto.setTotal(users.size());
			for (UserDto userDto : users) {
				try {
					ValidatorHelper.validate(userDto);
					AppUser save = dtoTAppUser(userDto);
					// save.setPassword(PasswordDecryptor);
					repo.save(save);
					resultDto.setImported(resultDto.getImported() + 1);
					log.info("User: " + userDto.getEmail() + " SAVED");

				} catch (Exception e) {
					log.error("User: " + userDto.getEmail() + "  NOT SAVED! \n" + e.getMessage());
					resultDto.setNonImported(resultDto.getNonImported() + 1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultDto;
	}

	public UserDto getUserProfile(String username) {
		return repo.findByUsername(username).map(this::entityTDto)
				.orElseThrow(NotFoundException::new);
	};

	private UserDto entityTDto(AppUser user) {
		return objectMapper.convertValue(user, UserDto.class);
	}

	private AppUser dtoTAppUser(UserDto dto) {
		return objectMapper.convertValue(dto, AppUser.class);
	}

	public List<AppUser> getAllUsers() {
		return repo.findAll();
	}
}
