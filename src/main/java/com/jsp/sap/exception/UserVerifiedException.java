package com.jsp.sap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserVerifiedException extends RuntimeException{
	private String message;

}
