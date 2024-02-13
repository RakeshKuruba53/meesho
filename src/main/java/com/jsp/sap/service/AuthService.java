package com.jsp.sap.service;

import org.springframework.http.ResponseEntity;

import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.entity.RefreshToken;
import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.AuthRequest;
import com.jsp.sap.requestdto.AuthResponse;
import com.jsp.sap.requestdto.OtpModel;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.util.ResponseStructure;
import com.jsp.sap.util.SimpleStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>>registerUser(UserRequest request);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpModel otp);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse httpServletResponse, String refreshToken, String accessToken);

	ResponseEntity<SimpleStructure> logout(String accessToken, String refreshToken, HttpServletResponse httpServletResponse);

	ResponseEntity<SimpleStructure> revokeAllDeviceAccess(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse);

	ResponseEntity<SimpleStructure> revokeOtherDeviceAccess(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse);

	ResponseEntity<SimpleStructure> refreshLogin(String accessToken, String refreshToken,
			HttpServletResponse httpServletResponse);

}
