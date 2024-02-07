package com.jsp.sap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNotVerifiedException extends RuntimeException {
private String message;
}
