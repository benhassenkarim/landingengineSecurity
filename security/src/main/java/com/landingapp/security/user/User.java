package com.landingapp.security.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class User {

	@Id @GeneratedValue
	private long id;
	@JoinColumn
	@OneToOne(cascade = CascadeType.ALL)
	private UserDetailsImpl userDetailsImpl;
	
	
	public User(UserDetailsImpl userDetailsImpl) {
		
		this.userDetailsImpl = userDetailsImpl;
	}

	public User() {
		
	}

	public UserDetailsImpl getUserDetailsImpl() {
		return userDetailsImpl;
	}
	
	public long getId() {
		return id;
	}
	public void setUserDetailsImpl(UserDetailsImpl userDetailsImpl) {
		this.userDetailsImpl = userDetailsImpl;
	}
	
}
