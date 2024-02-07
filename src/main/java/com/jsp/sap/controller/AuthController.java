package com.jsp.sap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;
import com.jsp.sap.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController {

	private AuthService authService;

	@PostMapping(value = "/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest request ) {
		return authService.registerUser(request);
	}
	
	

}
