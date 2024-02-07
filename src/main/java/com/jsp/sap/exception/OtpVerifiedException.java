package com.jsp.sap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OtpVerifiedException extends RuntimeException {
	private String message;
}
