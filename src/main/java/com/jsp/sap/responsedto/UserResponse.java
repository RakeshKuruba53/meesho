package com.jsp.sap.responsedto;

import com.jsp.sap.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class UserResponse {
	private int userId;
	private String userName;
	private String email;
	private UserRole userRole;
	private boolean isEmailVerified;
	private boolean isDeleted;


}
