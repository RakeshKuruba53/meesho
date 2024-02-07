package com.jsp.sap.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jsp.sap.entity.Customer;
import com.jsp.sap.entity.Seller;
import com.jsp.sap.entity.User;
import com.jsp.sap.enums.UserRole;
import com.jsp.sap.exception.OtpVerifiedException;
import com.jsp.sap.exception.UserExistException;
import com.jsp.sap.exception.UserNotVerifiedException;
import com.jsp.sap.exception.UserVerifiedException;
import com.jsp.sap.repository.CustomerRepo;
import com.jsp.sap.repository.SellerRepo;
import com.jsp.sap.repository.UserRepo;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;
import com.jsp.sap.util.ResponseStructure;

import lombok.AllArgsConstructor;
import lombok.Builder;
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{
	SellerRepo sellerRepo;
	CustomerRepo customerRepo;
	ResponseStructure<UserResponse> responseStructure;
	UserRepo userRepo;
	
	private UserResponse mapToUserResponse(User request) {
		return UserResponse.builder()
		.email(request.getEmail())
		.userRole(request.getUserRole())
		.build();
	}
	static <T extends User>  T mapToUser(UserRequest request) {
	 User user=null;
	 switch (request.getUserRole()) {
		case SELLER: {
			user=new Seller();
			break;
		}
		case CUSTOMER:{
			user= new Customer();
			break;
		}
	}
	 user.setEmail(request.getEmail());
	 user.setPassword(request.getPassword());
	 user.setUserRole(request.getUserRole());
	 String[] split = request.getEmail().split("@");
	 user.setUserName(split[0]);
	 return (T) user;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest user) {
		User user2 = mapToUser(user);
		if(userRepo.existsByEmail(user.getEmail())) {
			if(user2.isEmailVerified()) {
				throw new OtpVerifiedException("verified");
			}
			else {
				throw new UserExistException("user Exist please verify");
		}
		}
		
		user2=mapToSaveRespective(user2);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserResponse(user2))
				.setMessage("please verify emailid"),HttpStatus.CREATED);
		
	}
		
				
		
	
	private User mapToSaveRespective(User user) {
		switch (user.getUserRole()){
		case SELLER-> {user=sellerRepo.save((Seller)user);}
		case CUSTOMER->{user=customerRepo.save((Customer)user);}
		default->
			throw new IllegalArgumentException("Unexpected value: " + user.getUserRole());
		}
		return user;
	}
	}
