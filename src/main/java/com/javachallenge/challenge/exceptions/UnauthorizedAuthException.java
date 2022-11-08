package com.javachallenge.challenge.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedAuthException extends RuntimeException{
	
    public UnauthorizedAuthException() {
        super();
    }

    public UnauthorizedAuthException(String message) {
        super(message);
    }

    public UnauthorizedAuthException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedAuthException(String message, Throwable cause) {
        super(message, cause);
    }


}
