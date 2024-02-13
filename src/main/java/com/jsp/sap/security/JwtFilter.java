package com.jsp.sap.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.exception.UserNotLoggedInException;
import com.jsp.sap.repository.AccessTokenRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class JwtFilter extends OncePerRequestFilter{
	private AccessTokenRepo accessTokenRepo;
	private JwtService jwtService;
	private CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String at=null;
		String rt=null;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null) {
			for(Cookie cookie:cookies) {
				if(cookie.getName().equals("at")) at=cookie.getValue();
				if(cookie.getName().equals("rt")) rt=cookie.getValue();
			}
			String username=null;
			if(at==null&&rt==null) {
			log.info("Authenticating the token....");
			Optional<AccessToken> accessToken = accessTokenRepo.findByTokenAndIsBlocked(at,false);
			username = jwtService.extractUsername(at);
			if(username==null) throw new UserNotLoggedInException();
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken
					(username, null,userDetails.getAuthorities());
			token.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(token);
			log.info("Autehnticated Successfully......");
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	
}
