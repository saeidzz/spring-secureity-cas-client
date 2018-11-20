package com.example.spring.cas.demo.service;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


public interface CustomeUserDetailService extends UserDetailsService,AuthenticationUserDetailsService {

}
