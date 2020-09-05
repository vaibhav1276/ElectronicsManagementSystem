package com.domain.ems.util;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.domain.ems.exception.ForbiddenException;
import com.domain.ems.repository.UserRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class CommonUtil {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;

	public boolean validateUserTokenRole(String authToken,String requiredRole)
			throws ForbiddenException, JsonParseException, JsonMappingException, IOException {
		boolean roleFlag= false;
		
		String roleName = jwtUtil.decodeJWTForRoles(authToken);
		if(roleName.equalsIgnoreCase(requiredRole)) {
			roleFlag = true ;
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
		return roleFlag;
	}

	public void checkForExistingUser(String username)
			throws Exception {
	
		com.domain.ems.models.UserModel.User user = userRepository.findByUsername(username);
		if (user != null) {
			throw new Exception("User already exists");	
		}
	}

}
