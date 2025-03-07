package com.javachallenge.challenge.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException e, WebRequest request) {
		return buildError(e, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
		return buildError(e, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e, WebRequest request) {
		return buildError(e, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
	}

	@ExceptionHandler(UnauthorizedAuthException.class)
	public ResponseEntity<Object> handleUnauthorizedAuthException(UnauthorizedAuthException e, WebRequest request) {
		return buildError(e, HttpStatus.UNAUTHORIZED, request);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException e, WebRequest request) {
		return buildError(e, HttpStatus.UNAUTHORIZED, request);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<Object> handleJwtException(JwtException e, WebRequest request) {
		return buildError(e, HttpStatus.UNAUTHORIZED, request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
		return buildError(e, HttpStatus.FORBIDDEN, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllExceptions(Exception e, WebRequest request) {
		return buildError(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(FileException.class)
	public ResponseEntity<Object> handleFileException(FileException e, WebRequest request) {
		return buildError(e, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<String> handleIOException(IOException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("An error occurred while processing the user file.");
	}
	private ResponseEntity<Object> buildError(Exception e, HttpStatus status, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", e.getMessage());
		body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());

		return ResponseEntity.status(status).body(body);
	}

}