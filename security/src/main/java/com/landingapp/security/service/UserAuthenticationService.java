package com.landingapp.security.service;

import java.util.Optional;

import com.landingapp.security.user.User;
import com.landingapp.security.user.UserDetailsImpl;

public interface UserAuthenticationService {

	Optional<String> login(String username,String password);
	User findByToken(String token);
    
	
	
}
