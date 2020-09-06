package com.domain.ems.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

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

	ObjectMapper om = new ObjectMapper();
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	// User APIs
	@PostMapping("/users")
	public User createUser(@RequestBody User user, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			commonUtil.checkForExistingUser(user.getUsername());
			return userRepository.save(user);
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	@GetMapping("/users")
	public List<User> getAllUsers(@RequestHeader("Authorization") String authorizationHeader)
			throws JsonParseException, JsonMappingException, ForbiddenException, IOException {
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			return (List<User>) userRepository.findAll();
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}

	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found :: " + userId));
			return ResponseEntity.ok().body(user);
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}

	}

	@DeleteMapping("/users/{id}")
	public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Admin")) {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found :: " + userId));
			userRepository.delete(user);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	// Gadgets APIs
	@PostMapping("/addGadget")
	public Gadgets addGadget(@RequestBody Gadgets gadget, @RequestHeader("Authorization") String authorizationHeader)
			throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			return gadgetRepository.save(gadget);
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	@GetMapping("/gadgets")
	public List<Gadgets> getAllGadgets(@RequestHeader("Authorization") String authorizationHeader)
			throws JsonParseException, JsonMappingException, ForbiddenException, IOException {
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			return (List<Gadgets>) gadgetRepository.findAll();
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}

	}

	@GetMapping("/gadgets/{id}")
	public ResponseEntity<Gadgets> getGadgetById(@PathVariable(value = "id") Long gadgetId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException {
		Gadgets gadgets = gadgetRepository.findById(gadgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Gadget not found :: " + gadgetId));
		return ResponseEntity.ok().body(gadgets);
	}

	@PatchMapping("/gadgets/{id}")
	public ResponseEntity<Gadgets> updateGadget(@PathVariable(value = "id") Long gadgetId,
			@Valid @RequestBody Gadgets gadgetDetails, @RequestHeader("Authorization") String authorizationHeader)
			throws ResourceNotFoundException, JsonParseException, JsonMappingException, IOException {
		Gadgets gadgets = gadgetRepository.findById(gadgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Gadget not found :: " + gadgetId));

		String roleName = jwtTokenUtil.decodeJWTForRoles(authorizationHeader);
		if (roleName.equalsIgnoreCase("Sales")) {
			if (gadgetDetails.getBrand() != null) {
				gadgets.setBrand(gadgetDetails.getBrand());
			}
			if (gadgetDetails.getCategory() != null) {
				gadgets.setCategory(gadgetDetails.getCategory());
			}
			if (gadgetDetails.getName() != null) {
				gadgets.setName(gadgetDetails.getName());
			}
			gadgets.setPrice(gadgetDetails.getPrice());
			gadgets.setInventory(gadgetDetails.getInventory());
		} else if (roleName.equalsIgnoreCase("Admin")) {
			gadgets.setInventory(gadgetDetails.getInventory());
		}

		final Gadgets updatedGadgets = gadgetRepository.save(gadgets);
		return ResponseEntity.ok(updatedGadgets);
	}

	@DeleteMapping("/gadget/{id}")
	public Map<String, Boolean> deleteGadget(@PathVariable(value = "id") Long gadgetId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			Gadgets gadgets = gadgetRepository.findById(gadgetId)
					.orElseThrow(() -> new ResourceNotFoundException("Gadgets not found :: " + gadgetId));
			gadgetRepository.delete(gadgets);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	// Accessories APIs
	@PostMapping("/accessory")
	public Accessories addAccessory(@RequestBody Accessories accessories,
			@RequestHeader("Authorization") String authorizationHeader) throws Exception {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			return accessoriesRepository.save(accessories);
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	@GetMapping("/accessories")
	public List<Accessories> getAllAccessories(@RequestHeader("Authorization") String authorizationHeader)
			throws JsonParseException, JsonMappingException, ForbiddenException, IOException {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")
				 || commonUtil.validateUserTokenRole(authorizationHeader, "User")) {
			return (List<Accessories>) accessoriesRepository.findAll();
		} 
		/*
		 * else if (commonUtil.validateUserTokenRole(authorizationHeader, "User")) {
		 * List<Accessories> accessoriesList = (List<Accessories>)
		 * accessoriesRepository.findAll(); List<Accessories> finalList = new
		 * ArrayList<>(); for (Accessories accessory : accessoriesList) { Accessories
		 * updatedAccessory = new Accessories(); String transformedJson =
		 * commonUtil.transformJson("transformDetails.json",
		 * om.writeValueAsString(accessory)); logger.info(transformedJson);
		 * updatedAccessory = om.readValue(transformedJson, Accessories.class);
		 * logger.info("Converted JSON " + om.writeValueAsString(updatedAccessory));
		 * finalList.add(updatedAccessory);
		 * 
		 * }
		 * 
		 * return finalList; }
		 */ else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	@GetMapping("/accessories/{id}")
	public ResponseEntity<Accessories> getAccessoryById(@PathVariable(value = "id") Long accId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {

		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			Accessories accessories = accessoriesRepository.findById(accId)
					.orElseThrow(() -> new ResourceNotFoundException("Accessory not found :: " + accId));
			return ResponseEntity.ok().body(accessories);
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
		}
	}

	@PatchMapping("/accessories/{id}")
	public ResponseEntity<Accessories> updateAccessories(@PathVariable(value = "id") Long accId,
			@Valid @RequestBody Accessories accessoriesDetails,
			@RequestHeader("Authorization") String authorizationHeader)
			throws ResourceNotFoundException, JsonParseException, JsonMappingException, IOException {
		Accessories accessories = accessoriesRepository.findById(accId)
				.orElseThrow(() -> new ResourceNotFoundException("Accessory not found :: " + accId));

		String roleName = jwtTokenUtil.decodeJWTForRoles(authorizationHeader);
		if (roleName.equalsIgnoreCase("Sales")) {
			if (accessoriesDetails.getBrand() != null) {
				accessories.setBrand(accessoriesDetails.getBrand());
			}
			if (accessoriesDetails.getCategory() != null) {
				accessories.setCategory(accessoriesDetails.getCategory());
			}
			if (accessoriesDetails.getName() != null) {
				accessories.setName(accessoriesDetails.getName());
			}
			accessories.setPrice(accessoriesDetails.getPrice());
			accessories.setInventory(accessoriesDetails.getInventory());
		} else if (roleName.equalsIgnoreCase("Admin")) {
			accessories.setInventory(accessoriesDetails.getInventory());
		}

		final Accessories updatedAccessory = accessoriesRepository.save(accessories);
		return ResponseEntity.ok(updatedAccessory);
	}

	@DeleteMapping("/accessories/{id}")
	public Map<String, Boolean> deleteAccessories(@PathVariable(value = "id") Long accessoryId,
			@RequestHeader("Authorization") String authorizationHeader) throws ResourceNotFoundException,
			JsonParseException, JsonMappingException, ForbiddenException, IOException {
		Accessories accessories = accessoriesRepository.findById(accessoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Accessory not found :: " + accessoryId));
		if (commonUtil.validateUserTokenRole(authorizationHeader, "Sales")) {
			accessoriesRepository.delete(accessories);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			throw new ForbiddenException("User doesn't have the required role for this operation");
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
