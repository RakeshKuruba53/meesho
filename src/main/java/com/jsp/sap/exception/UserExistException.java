package com.jsp.sap.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserExistException extends RuntimeException {
private String message;
}
