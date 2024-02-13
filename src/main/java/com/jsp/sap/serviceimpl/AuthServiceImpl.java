package com.jsp.sap.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jsp.sap.cache.CacheStore;
import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.entity.Customer;
import com.jsp.sap.entity.RefreshToken;
import com.jsp.sap.entity.Seller;
import com.jsp.sap.entity.User;
import com.jsp.sap.exception.InvalidOtpException;
import com.jsp.sap.exception.OtpExpiredException;
import com.jsp.sap.exception.SessionExpiredException;
import com.jsp.sap.exception.UserExistException;
import com.jsp.sap.exception.UserLoggedInException;
import com.jsp.sap.exception.UserLoggedOutException;
import com.jsp.sap.exception.UserNotLoggedInException;
import com.jsp.sap.repository.AccessTokenRepo;
import com.jsp.sap.repository.CustomerRepo;
import com.jsp.sap.repository.RefreshTokenRepo;
import com.jsp.sap.repository.SellerRepo;
import com.jsp.sap.repository.UserRepo;
import com.jsp.sap.requestdto.AuthRequest;
import com.jsp.sap.requestdto.AuthResponse;
import com.jsp.sap.requestdto.OtpModel;
import com.jsp.sap.requestdto.UserRequest;
import com.jsp.sap.responsedto.UserResponse;
import com.jsp.sap.security.JwtService;
import com.jsp.sap.service.AuthService;
import com.jsp.sap.util.CookieManager;
import com.jsp.sap.util.MessageStructure;
import com.jsp.sap.util.ResponseStructure;
import com.jsp.sap.util.SimpleStructure;

import io.jsonwebtoken.Jws;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

	private SellerRepo sellerRepo;

	private CustomerRepo customerRepo;

	private ResponseStructure<UserResponse> responseStructure;

	private UserRepo userRepo;

	private CacheStore<String> otpcacheStore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;

	private PasswordEncoder encoder;

	private AuthenticationManager authenticationManager;

	private CookieManager cookieManager;

	private JwtService jwtService;

	private AccessTokenRepo accessTokenRepo;

	private RefreshTokenRepo refreshTokenRepo;

	private ResponseStructure<AuthResponse> authResponseStructure;
	@Autowired
	private SimpleStructure simpleStructure;



	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;
	@Value("${myapp.refresh.expiry}")
	private int refreshexpiryInSeconds;


	public  <T extends User>  T mapToUser(UserRequest request) {
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
		user.setPassword(encoder.encode(request.getPassword()));
		user.setUserRole(request.getUserRole());
		String[] split = request.getEmail().split("@");
		user.setUserName(split[0]);
		return (T) user;
	}



	public AuthServiceImpl(SellerRepo sellerRepo, CustomerRepo customerRepo,
			ResponseStructure<UserResponse> responseStructure, UserRepo userRepo, CacheStore<String> otpcacheStore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, PasswordEncoder encoder,
			AuthenticationManager authenticationManager, CookieManager cookieManager, JwtService jwtService,
			AccessTokenRepo accessTokenRepo, RefreshTokenRepo refreshTokenRepo,
			ResponseStructure<AuthResponse> authResponseStructure) {
		super();
		this.sellerRepo = sellerRepo;
		this.customerRepo = customerRepo;
		this.responseStructure = responseStructure;
		this.userRepo = userRepo;
		this.otpcacheStore = otpcacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.encoder = encoder;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTokenRepo = accessTokenRepo;
		this.refreshTokenRepo = refreshTokenRepo;
		this.authResponseStructure = authResponseStructure;
	}



	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest request) {

		if(userRepo.existsByEmail(request.getEmail())) 
			throw new UserExistException("User already Present with the given email");

		String otp = generateOtp();
		log.info(otp);
		User user = mapToUser(request);
		userCacheStore.add(request.getEmail(),user );
		otpcacheStore.add(request.getEmail(), otp);
		try {
			sendOtpToMail(user, otp);
		} catch (MessagingException e) {
			e.printStackTrace();

		}

		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserResponse(user))
				.setMessage("please verify otp sent on email id"),HttpStatus.CREATED);

	}
	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse httpServletResponse,String refreshToken,String accesstoken) {
		
		if(accesstoken!=null||refreshToken!=null) throw new UserLoggedInException("User Already Login");

		String userName = authRequest.getEmail().split("@")[0];

		UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				userName, authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(passwordAuthenticationToken);
		if (!authentication.isAuthenticated())
			throw new UsernameNotFoundException(" failed to authenticate the user");
		else {
			// generating the cookies and authresponse and returning to the client
			return userRepo.findByUserName(userName).map(user -> {
				grantAccess(httpServletResponse, user);
				ResponseStructure<AuthResponse> responseStructure = new ResponseStructure<>();
				return new ResponseEntity<ResponseStructure<AuthResponse>>(responseStructure
						.setStatus(HttpStatus.OK.value()).setMessage("login sucessfull")
						.setData(AuthResponse.builder().userId(user.getUserId()).username(user.getUserName())
								.role(user.getUserRole().name()).isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
								.refreshExpiration(LocalDateTime.now().plusSeconds(refreshexpiryInSeconds)).build()),
						HttpStatus.OK);
			}).get();
		}
	}
	@Override
	public ResponseEntity<SimpleStructure> logout(String accessToken,String refreshToken,
			HttpServletResponse httpServletResponse) {

		if(accessToken==null&&refreshToken==null)
			throw new UserNotLoggedInException("Not logged In",HttpStatus.NOT_FOUND);


		accessTokenRepo.findByToken(accessToken).ifPresent(accessToken1->{
			accessToken1.setBlocked(true);
			accessTokenRepo.save(accessToken1);
		});
		httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("at", "")));

		refreshTokenRepo.findByToken(refreshToken).ifPresent(refreshToken1->{
			refreshToken1.setBlocked(true);
			refreshTokenRepo.save(refreshToken1);
		});
		httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("rt", "")));
		simpleStructure.setMessage(accessToken);
		simpleStructure.setStatus(HttpStatus.GONE.value());

		return new ResponseEntity<SimpleStructure>(simpleStructure,HttpStatus.GONE);
	}


	@Override
	public ResponseEntity<SimpleStructure> revokeAllDeviceAccess(String accessToken,
			String refreshToken, HttpServletResponse httpServletResponse) {
		System.out.println(2);
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if(name==null) throw new UsernameNotFoundException("User not found");
		userRepo.findByUserName(name).ifPresent(user->{
			blockAccessTokens(accessTokenRepo.findAllByUserAndIsBlocked(user,false));
			blockRefreshTokens(refreshTokenRepo.findAllByUserAndIsBlocked(user,false));
			httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("at", "")));
			httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("rt", "")));
		});
		System.out.println(3);
		simpleStructure.setMessage("revoked all Tokens");
		simpleStructure.setStatus(HttpStatus.GONE.value());
		return new ResponseEntity<SimpleStructure>(simpleStructure, HttpStatus.OK);
	}
	@Override
	public ResponseEntity<SimpleStructure> revokeOtherDeviceAccess(String accessToken,
			String refreshToken, HttpServletResponse httpServletResponse) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if(name==null) throw new UsernameNotFoundException("User not found");
		userRepo.findByUserName(name).ifPresent(user->{
			blockAccessTokens(accessTokenRepo.findAllByUserAndIsBlockedAndTokenNot( user,false,accessToken));
			blockRefreshTokens(refreshTokenRepo.findAllByUserAndIsBlockedAndTokenNot(user,false,refreshToken));
		});
		simpleStructure.setMessage("Revoked other device access");
		simpleStructure.setStatus(HttpStatus.GONE.value());
		return new ResponseEntity<SimpleStructure>(simpleStructure,HttpStatus.OK);
	}
	@Override
	public ResponseEntity<SimpleStructure> refreshLogin(String accessToken,
			String refreshToken, HttpServletResponse httpServletResponse) {

		accessTokenRepo.findByToken(accessToken).ifPresent(at->{
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});

		if(refreshToken==null) throw new UserLoggedOutException();

		refreshTokenRepo.findByToken(refreshToken).ifPresent(rt->{
			grantAccess(httpServletResponse,rt.getUser());

			revokeOtherDeviceAccess(accessToken, refreshToken, httpServletResponse);

			httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("at", "")));
			httpServletResponse.addCookie(CookieManager.invalidateCookie(new Cookie("rt", "")));
		}); 

		simpleStructure.setMessage("tokens are refreshed");		
		simpleStructure.setStatus(HttpStatus.FOUND.value());

		return new ResponseEntity<SimpleStructure>(simpleStructure,HttpStatus.FOUND);





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
		try {
			confirmMail(user);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserResponse(user))
				.setMessage("Registration succesful!!"),HttpStatus.CREATED);
	}
	public String generateOtp() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}
	public void sendOtpToMail(User user,String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Complete Your Registration to Meesho")
				.sentDate(new Date())
				.text(
						"Hey, "+user.getUserName()
						+" Good to See You Interested In Meesho,"
						+" Complete your Registration Using Otp <br>"
						+"<h1>"+otp+"</h1><br>"
						+"Note : The Otp Expires In One minute"
						+"<br><br>"
						+"with best Regards<br>"
						+"Meesho"
						)
				.build());
	}
	//--------------------------------------------------------------------------------------------------------	
	private void blockAccessTokens(List<AccessToken> accesstokens) {
		for(AccessToken accessToken:accesstokens) {
			accessToken.setBlocked(true);
			accessTokenRepo.save(accessToken);
		}
	}
	private void blockRefreshTokens(List<RefreshToken> refreshtokens) {
		for(RefreshToken refreshToken:refreshtokens) {
			refreshToken.setBlocked(true);
			refreshTokenRepo.save(refreshToken);
		}
	}
	private void grantAccess(HttpServletResponse httpServletResponse,User user) {
		//generating access and refresh tokens
		String accesToken = jwtService.generateAccesToken(user.getUserName());
		String refreshToken = jwtService.generateRefreshToken(user.getUserName());

		// adding and refresh tokens cookies to the response
		Cookie cookie=cookieManager.configure(new Cookie("at", accesToken), accessExpiryInSeconds);
		httpServletResponse.addCookie(cookie);
		Cookie cookie2 = cookieManager.configure(new Cookie("rt", refreshToken), accessExpiryInSeconds);
		httpServletResponse.addCookie(cookie2);

		//saving the access and refresh cookie into the database
		accessTokenRepo.save(AccessToken.builder()
				.token(accesToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.user(user)
				.build());
		refreshTokenRepo.save(RefreshToken.builder()
				.token(refreshToken)
				.isBlocked(false)
				.user(user)
				.expiration(LocalDateTime.now().plusSeconds(refreshexpiryInSeconds))
				.build());
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
	private void confirmMail(User user) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Thanks for verifying your Email")
				.sentDate(new Date())
				.text(
						"Hey, "+user.getUserName()+
						"welcome to Meesho"
						+"<br><br>"
						+" with best Regards<br>"
						+"Meesho"
						)
				.build());
	}

	@Async
	private void sendMail(MessageStructure messageStructure ) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimeMessage, true);
		helper.setTo(messageStructure.getTo());
		helper.setSubject(messageStructure.getSubject());
		helper.setSentDate(messageStructure.getSentDate());
		helper.setText(messageStructure.getText(),true);
		javaMailSender.send(mimeMessage);
	}
	private UserResponse mapToUserResponse(User request) {
		return UserResponse.builder()
				.email(request.getEmail())
				.userRole(request.getUserRole())
				.build();
	}




}
