package com.jsp.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;

	@PostMapping(value = "/users/register")
	public UserResponse registerUser(@RequestBody UserRequest request ) {
		return authService.registerUser(request);

	}

}
