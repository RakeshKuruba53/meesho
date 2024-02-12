package com.jsp.sap.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jsp.sap.entity.AccessToken;
import com.jsp.sap.entity.RefreshToken;
import com.jsp.sap.repository.AccessTokenRepo;
import com.jsp.sap.repository.RefreshTokenRepo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
public class ScheduledJobsClass {
	@Autowired
	AccessTokenRepo accessTokenRepo;
	@Autowired
	RefreshTokenRepo refreshTokenRepo;
	
	@Scheduled(fixedDelay = 100000l)
	public void deleteBlockedUsers() 
	{
		List<AccessToken> blockedTokens = accessTokenRepo.findByIsBlocked(true);
		for(AccessToken accessToken:blockedTokens) {
			accessTokenRepo.delete(accessToken);
		}
		
		List<RefreshToken> blockedTokens1 = refreshTokenRepo.findByIsBlocked(true);
		
			for(RefreshToken refreshToken:blockedTokens1) {
				refreshTokenRepo.delete(refreshToken);
			}
		
		
	}
}


