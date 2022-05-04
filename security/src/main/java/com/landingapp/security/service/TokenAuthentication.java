package com.landingapp.security.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.landingapp.security.model.UserRepository;
import com.landingapp.security.user.User;
import com.landingapp.security.user.UserDetailsImpl;
@Component
public class TokenAuthentication implements UserAuthenticationService{

	private final TokenService tokenService;
	private final UserRepository userRepository; 
	@Autowired
	public TokenAuthentication(TokenService tokenService, UserRepository userRepository) {
		
		this.tokenService = tokenService;
		this.userRepository = userRepository;
	}
	
	@Override
	public Optional<String> login(String username, String password) {
		
		return Optional.ofNullable(userRepository.findByUserDetails_Username(username))
				       .filter(user -> user.get().getUserDetailsImpl().getPassword().equals(password))
				       .map(user -> tokenService.expiring(ImmutableMap.of("username",username)));
	}

	@Override
	public User findByToken(String token) {
		Map<String,String> result= tokenService.verify(token);
		return userRepository.findByUserDetails_Username(result.get("username")).get();
	}

	

	

}
