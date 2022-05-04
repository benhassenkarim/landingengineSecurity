package com.landingapp.security.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.landingapp.security.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserDetails_Username(String username);
}
