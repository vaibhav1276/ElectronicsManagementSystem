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

	public boolean validateAdminUserToken(String authToken)
			throws ForbiddenException, JsonParseException, JsonMappingException, IOException {
		String username = null;
		String jwt = null;
		boolean roleFlag = false;

		if (authToken != null && authToken.startsWith("Bearer ")) {
			jwt = authToken.substring(7);
			username = jwtUtil.extractUsername(jwt);
			logger.info("Username " + username);
			if (!jwtUtil.decodeJWTForRoles(jwt)) {
				throw new ForbiddenException("User dosn't have the required authority for this operation");
			} else {
				roleFlag = true;
			}

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
