package com.domain.ems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception{

	private static final long serialVersionUID = 1L;

	public ForbiddenException(String message){
    	super(message);
    }
}
