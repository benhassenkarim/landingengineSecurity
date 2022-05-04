package com.landingapp.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.landingapp.security.model.UserRepository;
import com.landingapp.security.service.UserAuthenticationService;
import com.landingapp.security.user.User;
import com.landingapp.security.user.UserDetailsImpl;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserRepository userRepository;
	private final UserAuthenticationService userAuthenticationService;
	@Autowired
	public UserController(UserRepository userRepository, UserAuthenticationService userAuthenticationService) {
		
		this.userRepository = userRepository;
		this.userAuthenticationService = userAuthenticationService;
	}
	
	@PostMapping("/register")
	public String register(@RequestBody UserDetailsImpl userDetails) {
		userRepository.save(new User(userDetails));
		return login(userDetails);
	}
	@PostMapping("login")
	public String login(@RequestBody UserDetailsImpl userDetails) {
		return userAuthenticationService
				.login(userDetails.getUsername(), userDetails.getPassword())
				.orElseThrow(()-> new RuntimeException("invalid login details"));
	}
}
