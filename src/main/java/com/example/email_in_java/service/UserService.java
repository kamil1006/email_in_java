package com.example.email_in_java.service;

import com.example.email_in_java.entity.User;
import com.example.email_in_java.entity.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
}
