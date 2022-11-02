package com.javachallenge.challenge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javachallenge.challenge.dto.BatchDto;
import com.javachallenge.challenge.dto.UserDto;
import com.javachallenge.challenge.model.UserRole;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.Name;
import net.datafaker.fileformats.Format;

@Slf4j
@Service
public class UserService {

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
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			List<UserDto> users = objectMapper
					.readValue(file.getInputStream(), new TypeReference<List<UserDto>>() {});
			for (UserDto userDto : users)
				log.info("++" + userDto.getEmail());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultDto;
	}
}
