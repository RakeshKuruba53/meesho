package com.jsp.sap.service;

import org.springframework.http.ResponseEntity;

import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.OtpModel;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.util.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>>registerUser(UserRequest request);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpModel otp);

}
