package com.javachallenge.challenge.utils;

import com.javachallenge.challenge.exceptions.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class ValidatorHelper {

	public static void validate(Object obj) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> violations = validator.validate(obj);

		if (violations.size() > 0)
			throw new BadRequestException("Validation Error:\n" + violations);
	}
}
