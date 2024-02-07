package com.jsp.sap.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.util.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>>registerUser(UserRequest request);

	

}
