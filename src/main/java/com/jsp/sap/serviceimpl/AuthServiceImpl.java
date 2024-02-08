package com.jsp.sap.serviceimpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jsp.sap.cache.CacheStore;
import com.jsp.sap.entity.Customer;
import com.jsp.sap.entity.Seller;
import com.jsp.sap.entity.User;
import com.jsp.sap.enums.UserRole;
import com.jsp.sap.exception.InvalidOtpException;
import com.jsp.sap.exception.OtpExpiredException;
import com.jsp.sap.exception.OtpVerifiedException;
import com.jsp.sap.exception.SessionExpiredException;
import com.jsp.sap.exception.UserExistException;
import com.jsp.sap.exception.UserNotVerifiedException;
import com.jsp.sap.exception.UserVerifiedException;
import com.jsp.sap.repository.CustomerRepo;
import com.jsp.sap.repository.SellerRepo;
import com.jsp.sap.repository.UserRepo;
import com.jsp.sap.requestdto.OtpModel;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.service.AuthService;
import com.jsp.sap.util.MessageStructure;
import com.jsp.sap.util.ResponseStructure;

import ch.qos.logback.core.encoder.Encoder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
@Service
@NoArgsConstructor
public class AuthServiceImpl implements AuthService{
	@Autowired
	SellerRepo sellerRepo;
	@Autowired
	CustomerRepo customerRepo;
	@Autowired
	ResponseStructure<UserResponse> responseStructure;
	@Autowired
	UserRepo userRepo;
	@Autowired
	private CacheStore<String> otpcacheStore;
	@Autowired
	private CacheStore<User> userCacheStore;
	@Autowired
	private JavaMailSender javaMailSender;
	

	
	
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
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest request) {
		
		if(userRepo.existsByEmail(request.getEmail())) 
			throw new UserExistException("User already Present with the given email");
		
		String otp = generateOtp();
		User user = mapToUser(request);
		userCacheStore.add(request.getEmail(),user );
		otpcacheStore.add(request.getEmail(), otp);
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserResponse(user))
				.setMessage("please verify otp"+otp),HttpStatus.CREATED);
		
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
	@Scheduled(fixedDelay = 1000000l)
	public void clearNonVerifiedUsers() {
		List<User> list = userRepo.findAll();
		List<User> deleted=new ArrayList<>();
		for(User user:list) {
			if(user.isEmailVerified()==false) {
				userRepo.delete(user);
			}
		}
	}
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpModel otpModel) {
		User user=userCacheStore.get(otpModel.getEmail());
		String otp = otpcacheStore.get(otpModel.getEmail());
		if(otp==null) throw new OtpExpiredException("otp Expired");

		if(user==null) throw new SessionExpiredException("Registration Session Expired");
				
		if(!otp.equals(otpModel.getOtp()))throw new InvalidOtpException("Invalid Otp");
		
			user.setEmailVerified(true);
			userRepo.save(user);
			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.CREATED.value())
					.setData(mapToUserResponse(user))
					.setMessage("Registration succesful!!"),HttpStatus.CREATED);
		}
		
	public String generateOtp() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}
	@Async
	private void sendMail(MessageStructure messageStructure ) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimeMessage, true);
		helper.setTo(messageStructure.getTo());
		helper.setSubject(messageStructure.getSubject());
		helper.setSentDate(messageStructure.getSentDate());
		helper.setText(messageStructure.getText());
		javaMailSender.send(mimeMessage);
	}
	
	}
