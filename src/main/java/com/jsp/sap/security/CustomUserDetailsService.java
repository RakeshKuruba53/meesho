package com.jsp.sap.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jsp.sap.repository.UserRepo;
import com.jsp.sap.serviceimpl.AuthServiceImpl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService{
UserRepo repo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	return repo.findByUserName(username).map(user-> new CustomUserDetails(user))
			.orElseThrow(()->new  UsernameNotFoundException("authenticate failed"));
			

	}

}
