package com.domain.ems.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.domain.ems.MyUserDetailsService;
import com.domain.ems.exception.ForbiddenException;
import com.domain.ems.exception.ResourceNotFoundException;
import com.domain.ems.models.Authentication.AuthenticationRequest;
import com.domain.ems.models.Authentication.AuthenticationResponse;
import com.domain.ems.models.UserModel.User;
import com.domain.ems.models.electonics.Accessories;
import com.domain.ems.models.electonics.Gadgets;
import com.domain.ems.repository.AccessoriesRepository;
import com.domain.ems.repository.GadgetRepository;
import com.domain.ems.repository.UserRepository;
import com.domain.ems.util.CommonUtil;
import com.domain.ems.util.JwtUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
	private AccessoriesRepository accessoriesRepository;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private CommonUtil commonUtil;

	// User APIs
	@PostMapping("/users")
	public User createUser(@RequestBody User user, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			commonUtil.checkForExistingUser(user.getUsername());
			return userRepository.save(user);
		} else {
			return null;
		}
	}

	@GetMapping("/users")
	public List<User> getAllUsers() {
		return (List<User>) userRepository.findAll();
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(
			@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found :: " + userId));
		return ResponseEntity.ok().body(user);
	}


	@DeleteMapping("/users/{id}")
	public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found :: " + userId));
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			userRepository.delete(user);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			return null;
		}
	}

	// Gadgets APIs
	@PostMapping("/addGadget")
	public Gadgets addGadget(@RequestBody Gadgets gadget, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			return gadgetRepository.save(gadget);
		} else {
			return null;
		}
	}

	@GetMapping("/gadgets")
	public List<Gadgets> getAllGadgets() {
		return (List<Gadgets>) gadgetRepository.findAll();
	}
	
	@GetMapping("/gadgets/{id}")
	public ResponseEntity<Gadgets> getGadgetById(
			@PathVariable(value = "id") Long gadgetId) throws ResourceNotFoundException {
		Gadgets gadgets = gadgetRepository.findById(gadgetId)
        .orElseThrow(() -> new ResourceNotFoundException("Gadget not found :: " + gadgetId));
		return ResponseEntity.ok().body(gadgets);
	}

	@DeleteMapping("/gadget/{id}")
	public Map<String, Boolean> deleteGadget(@PathVariable(value = "id") Long gadgetId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		Gadgets gadgets = gadgetRepository.findById(gadgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Gadgets not found :: " + gadgetId));
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			gadgetRepository.delete(gadgets);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			return null;
		}
	}

	// Accessories APIs
	@PostMapping("/addAccessory")
	public Accessories addAccessory(@RequestBody Accessories accessories,
			@RequestHeader("Authorization") String authorizationHeader) throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			return accessoriesRepository.save(accessories);
		} else {
			return null;
		}
	}

	@GetMapping("/accessories")
	public List<Accessories> getAllAccessories() {
		return (List<Accessories>) accessoriesRepository.findAll();
	}
	
	@GetMapping("/accessories/{id}")
	public ResponseEntity<Accessories> getAccessoryById(
			@PathVariable(value = "id") Long accId) throws ResourceNotFoundException {
		Accessories accessories = accessoriesRepository.findById(accId)
        .orElseThrow(() -> new ResourceNotFoundException("Accessory not found :: " + accId));
		return ResponseEntity.ok().body(accessories);
	}

	@DeleteMapping("/gadget/{id}")
	public Map<String, Boolean> deleteAccessories(@PathVariable(value = "id") Long accessoryId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		Accessories accessories = accessoriesRepository.findById(accessoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Accessory not found :: " + accessoryId));
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			accessoriesRepository.delete(accessories);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			return null;
		}
	}

	// Authenticate API
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
