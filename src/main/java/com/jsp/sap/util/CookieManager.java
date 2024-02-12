package com.jsp.sap.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
@Component
public class CookieManager {
	@Value("${myapp.domain}")
	private String domain;
	public Cookie configure(Cookie cookie,int expirationTimeInSeconds) {
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expirationTimeInSeconds);
		cookie.setPath("/");
		return cookie;
	}
	public static Cookie invalidateCookie(Cookie cookie) {
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}

}
