package com.jsp.sap.service;

import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;

public interface AuthService {

	UserResponse registerUser(UserRequest request);

}
