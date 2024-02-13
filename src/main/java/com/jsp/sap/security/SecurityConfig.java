package com.jsp.sap.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
	CustomUserDetailsService customUserdetails;
	
	private JwtFilter jwtFilter;

	@Bean
	PasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
		return security.csrf(csrf->csrf.disable())
				.authorizeHttpRequests(auth->auth.requestMatchers("/**").permitAll().anyRequest().authenticated())
				.sessionManagement(management-> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtFilter ,UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider  provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserdetails);
		provider.setPasswordEncoder(passEncoder());

		return provider;
}
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
