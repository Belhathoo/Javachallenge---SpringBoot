package com.javachallenge.challenge.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.javachallenge.challenge.exceptions.BadRequestException;

public class ValidatorHelper {

	public static void validate(Object obj) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> violations = validator.validate(obj);

		if (violations.size() > 0)
			throw new BadRequestException("Validation Error:\n" + violations);
	}
}
