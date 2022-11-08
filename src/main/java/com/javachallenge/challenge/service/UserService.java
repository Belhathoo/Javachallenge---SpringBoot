package com.javachallenge.challenge.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.dto.AppUserDto;
import com.javachallenge.challenge.exceptions.BadRequestException;
import com.javachallenge.challenge.exceptions.UserNotFoundException;
import com.javachallenge.challenge.model.AppUser;
import com.javachallenge.challenge.model.UserRole;
import com.javachallenge.challenge.repository.UserRepository;
import com.javachallenge.challenge.utils.ValidatorHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.Name;
import net.datafaker.fileformats.Format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository repo;
	private final ObjectMapper objectMapper;
	private final PasswordEncoder passwordEncoder;

	private AppUserDto entityTDto(AppUser user) {
		return objectMapper.convertValue(user, AppUserDto.class);
	}

	private AppUser dtoTAppUser(AppUserDto dto) {
		return objectMapper.convertValue(dto, AppUser.class);
	}

	public AppUserDto getUserProfile(String username) {

		return repo.findByUsername(username).map(this::entityTDto)
				.orElseThrow(UserNotFoundException::new);
	}

	public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
		Optional<AppUser> appUser = (new EmailValidator().isValid(username, null))
				? repo.findByEmail(username)
				: repo.findByUsername(username);

		if (appUser.isEmpty()) {
			log.error("User " + username + " not found");
			throw new UserNotFoundException("User " + username + " not found");
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(appUser.get().getRole().name()));

		return new User(
				appUser.get().getUsername(),
				appUser.get().getPassword(),
				authorities);
	}

	public String userGenerate(Integer count) {
		if (count < 1) {
			throw new BadRequestException("Parameter \"count\" should be positive");
		}

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
			List<AppUserDto> users = objectMapper
					.readValue(file.getInputStream(), new TypeReference<List<AppUserDto>>() {
					});
			resultDto.setTotal(users.size());
			for (AppUserDto userDto : users) {
				try {
					ValidatorHelper.validate(userDto);
					AppUser save = dtoTAppUser(userDto);
					save.setPassword(passwordEncoder.encode(userDto.getPassword()));
					repo.save(save);
					resultDto.setImported(resultDto.getImported() + 1);
					log.info("User: " + userDto.getUsername() + " SAVED");

				} catch (Exception e) {
					log.error("User: " + userDto.getUsername() + "  NOT SAVED! \n" + e.getMessage());
					resultDto.setNonImported(resultDto.getNonImported() + 1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultDto;
	}

}
