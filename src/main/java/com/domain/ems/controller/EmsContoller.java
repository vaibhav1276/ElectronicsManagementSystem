package com.domain.ems.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.domain.ems.MyUserDetailsService;
import com.domain.ems.exception.ForbiddenException;
import com.domain.ems.models.AuthenticationRequest;
import com.domain.ems.models.AuthenticationResponse;
import com.domain.ems.models.User;
import com.domain.ems.repository.UserRepository;
import com.domain.ems.util.JwtUtil;

@RestController
public class EmsContoller {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MyUserDetailsService userDetailsService;
	
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtUtil jwtUtil;


	@PostMapping("/users")
	public User createUser( @RequestBody User user,@RequestHeader("Authorization") String authorizationHeader) throws Exception {
	
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            logger.info("Username "+username);
            if(!jwtUtil.decodeJWTForRoles(jwt)) {
            	throw new ForbiddenException("User dosn't have the required authority to create a user");
            }
           
        }

        return userRepository.save(user);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}


		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

}
