package com.domain.ems.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.domain.ems.models.jwt.Role;
import com.domain.ems.models.jwt.Token;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	private String SECRET_KEY = "secret";

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", userDetails.getUsername());
		claims.put("Role", userDetails.getAuthorities());
		return createToken(claims, userDetails.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String decodeJWTForRoles(String authToken) throws JsonParseException, JsonMappingException, IOException {
		logger.info("Inside Decoder");
		String roleName = null;
		if (authToken != null && authToken.startsWith("Bearer ")) {
			String jwtToken = authToken.substring(7);
			java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
			String[] parts = jwtToken.split("\\.");
			String payloadJson = new String(decoder.decode(parts[1]));
			ObjectMapper om = new ObjectMapper();
			Token token = om.readValue(payloadJson, Token.class);
			for (Role role : token.getRole()) {
				if (role.getAuthority() != null) {
					roleName = role.getAuthority();
					logger.info("roleName  : " + roleName);
				}
			}
		}
		return roleName;
	}

}