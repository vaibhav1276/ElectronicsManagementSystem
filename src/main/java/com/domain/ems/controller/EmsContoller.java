package com.domain.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.domain.ems.MyUserDetailsService;
import com.domain.ems.models.Authentication.AuthenticationRequest;
import com.domain.ems.models.Authentication.AuthenticationResponse;
import com.domain.ems.models.UserModel.User;
import com.domain.ems.models.electonics.Gadgets;
import com.domain.ems.repository.GadgetRepository;
import com.domain.ems.repository.UserRepository;
import com.domain.ems.util.CommonUtil;
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
	private GadgetRepository gadgetRepository;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private CommonUtil commonUtil;

	@PostMapping("/users")
	public User createUser(@RequestBody User user, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader,"Admin")) {
			commonUtil.checkForExistingUser(user.getUsername());
			return userRepository.save(user);
		} else {
			return null;
		}
	}
	
	@PostMapping("/addGadget")
	public Gadgets addGadget(@RequestBody Gadgets gadget, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader,"Sales")) {
			return gadgetRepository.save(gadget);
		} else {
			return null;
		}
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

}
