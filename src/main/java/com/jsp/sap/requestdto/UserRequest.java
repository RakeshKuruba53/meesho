package com.jsp.sap.requestdto;

import com.jsp.sap.enums.UserRole;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequest {
	
	private String email;
	private String password;
	private UserRole userRole;
	


}
