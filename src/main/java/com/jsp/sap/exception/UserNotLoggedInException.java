package com.jsp.sap.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNotLoggedInException extends RuntimeException {
	String message;
	HttpStatus httpStatus;
	String data;
	



}
