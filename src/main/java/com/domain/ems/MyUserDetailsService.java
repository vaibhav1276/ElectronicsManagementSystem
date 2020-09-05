package com.domain.ems;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.domain.ems.exception.ResourceNotFoundException;
import com.domain.ems.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;

	ObjectMapper om = new ObjectMapper();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.domain.ems.models.UserModel.User user = userRepository.findByUsername(username);
		if (user != null) {
			try {
				logger.info("User Details : " + om.writeValueAsString(user));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
			List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
			updatedAuthorities.add(authority);
			return new User(user.getUsername(), user.getPassword(), updatedAuthorities);
		}
		else {
			throw new UsernameNotFoundException("Request User Not Found");
		}
	}
}