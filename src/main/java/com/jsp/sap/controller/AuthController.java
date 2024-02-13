package com.jsp.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.entity.RefreshToken;
import com.jsp.sap.entity.User;
import com.jsp.sap.requestdto.AuthRequest;
import com.jsp.sap.requestdto.AuthResponse;
import com.jsp.sap.requestdto.OtpModel;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;
import com.jsp.sap.util.ResponseStructure;
import com.jsp.sap.util.SimpleStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/fp/v1")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping(value = "/users/register")
	
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest request ) {
		return authService.registerUser(request);
	}
	@PostMapping(value = "/verify-Otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(@RequestBody OtpModel otp){
		return authService.verifyOtp(otp);
	}
	@PostMapping(value = "/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,HttpServletResponse httpServletResponse,@CookieValue(name ="rt",required =  false) String refreshToken,@CookieValue (name ="at",required = false)String accessToken){

		return authService.login(authRequest,httpServletResponse,refreshToken,accessToken);
	}
	
	@PostMapping(value = "/logout")
	public ResponseEntity<SimpleStructure> logout(@CookieValue(name ="rt",required =  false) String refreshToken,@CookieValue (name ="at",required = false)String accessToken,HttpServletResponse httpServletResponse){
		return authService.logout(accessToken,refreshToken,httpServletResponse);
	}
	@DeleteMapping(value = "/revokeAll")
   public ResponseEntity<SimpleStructure> revokeAllDeviceAccess(@CookieValue(name ="rt",required =  false) String refreshToken,@CookieValue (name ="at",required = false)String accessToken,HttpServletResponse httpServletResponse){
		return authService.revokeAllDeviceAccess(accessToken,refreshToken,httpServletResponse);
	}
	@DeleteMapping(value = "/revokeOther")
	public ResponseEntity<SimpleStructure> revokeOtherDeviceAccess(@CookieValue(name ="rt",required =  false) String refreshToken,@CookieValue (name ="at",required = false)String accessToken,HttpServletResponse httpServletResponse){
		System.out.println("1");
		return authService.revokeOtherDeviceAccess(accessToken,refreshToken,httpServletResponse);
}
	@PostMapping(value = "/refreshlogin")
	public ResponseEntity<SimpleStructure> refreshLogin(String accessToken,
			String refreshToken, HttpServletResponse httpServletResponse){
		return authService.refreshLogin(accessToken,refreshToken,httpServletResponse);
	}
}
