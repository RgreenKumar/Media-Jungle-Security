package com.VsmartEngine.MediaJungle.userregister;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.VsmartEngine.MediaJungle.LogManagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class JwtUtil {
	
	 @Autowired
	 private JwtConfig jwtConfig;
	 
	 @org.springframework.beans.factory.annotation.Value("${jwt.expiration-ms:86400000}")
	 private long jwtExpirationMs = 86400000L;

	 private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	// =======================================
	// Internship Security Enhancement
	// Feature: Role Based Access Control
	// ISO27001 Control: Access Control
	// =======================================
	// RBAC: Generate JWT token containing username subject and role claim
	public String generateToken(String username, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
		String safeRole = (role != null && !role.isEmpty()) ? role : com.VsmartEngine.MediaJungle.security.UserRole.USER.name();
		return Jwts.builder()
			.setSubject(username)
			.claim("username", username)
			.claim("role", safeRole) // RBAC role claim
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey())
			.compact();
	}

	/**
	 * Generates a short-lived (5 minute) intermediate token for 2FA/MFA challenge.
	 */
	public String generateMfaTempToken(String username, String role, String userType, Long userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + 300000); // 5 minutes
		return Jwts.builder()
			.setSubject(username)
			.claim("username", username)
			.claim("role", role)
			.claim("userType", userType)
			.claim("userId", userId)
			.claim("mfa_pending", true)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS256, jwtConfig.getSecretKey())
			.compact();
	}

	    public boolean validateToken(String token) {
	        try {
	            String cleanToken = token;
	            if (cleanToken != null && cleanToken.startsWith("Bearer ")) {
	                cleanToken = cleanToken.substring(7);
	            }
	            Jwts.parser()
	                .setSigningKey(jwtConfig.getSecretKey())
	                .parseClaimsJws(cleanToken);
	            return true;
	        } catch (Exception e) {
	        	logger.error("", e);
	            return false;
	        }
	    }

	    public String getUsernameFromToken(String token) {
	        try {
	            Claims claims = getAllClaimsFromToken(token);
	            if (claims == null) return null;
	            String username = claims.get("username", String.class);
	            return username != null ? username : claims.getSubject();
	        } catch (Exception e) {
	        	logger.error("", e);
	            return null;
	        }
	    }

	    public String getRoleFromToken(String token) {
	        Claims claims = getAllClaimsFromToken(token);
	        return claims != null ? claims.get("role", String.class) : null;
	    }

	    public boolean isMfaPending(String token) {
	        Claims claims = getAllClaimsFromToken(token);
	        return claims != null && Boolean.TRUE.equals(claims.get("mfa_pending", Boolean.class));
	    }

	    public Claims getMfaClaims(String token) {
	        Claims claims = getAllClaimsFromToken(token);
	        if (claims != null && Boolean.TRUE.equals(claims.get("mfa_pending", Boolean.class))) {
	            return claims;
	        }
	        return null;
	    }

	    private Claims getAllClaimsFromToken(String token) {
	        String cleanToken = token;
	        if (cleanToken != null && cleanToken.startsWith("Bearer ")) {
	            cleanToken = cleanToken.substring(7);
	        }
	        return Jwts.parser()
	                .setSigningKey(jwtConfig.getSecretKey())
	                .parseClaimsJws(cleanToken)
	                .getBody();
	    }
}
