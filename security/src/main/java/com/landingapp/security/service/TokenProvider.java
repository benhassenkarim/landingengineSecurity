package com.landingapp.security.service;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.landingapp.security.user.User;
@Service
public class TokenProvider extends AbstractUserDetailsAuthenticationProvider {
	private final UserAuthenticationService userAuthenticationService;
	

	public TokenProvider(UserAuthenticationService userAuthenticationService) {
		
		this.userAuthenticationService = userAuthenticationService;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
		
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		
		final Object token = authentication.getCredentials();
		return Optional.ofNullable(token)
				.map(String::valueOf)
				.map(userAuthenticationService::findByToken)
				.map(User::getUserDetailsImpl).get();
	}

}
