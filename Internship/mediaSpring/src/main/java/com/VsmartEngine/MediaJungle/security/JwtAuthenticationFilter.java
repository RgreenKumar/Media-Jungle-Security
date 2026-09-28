// =======================================
// Internship Security Enhancement
// Feature: Role Based Access Control
// ISO27001 Control: Access Control
// =======================================
package com.VsmartEngine.MediaJungle.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.VsmartEngine.MediaJungle.userregister.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// RBAC: JWT Filter to populate SecurityContext with User Principal and GrantedAuthorities
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Constructor Injection
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && !authHeader.trim().isEmpty()) {
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                token = authHeader;
            }
            try {
                if (jwtUtil.validateToken(token) && !jwtUtil.isMfaPending(token)) {
                    username = jwtUtil.getUsernameFromToken(token);
                }
            } catch (Exception e) {
                // Ignore invalid tokens; request will proceed unauthenticated
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check User Role from token
            String roleStr = jwtUtil.getRoleFromToken(token);
            UserRole role = UserRole.fromString(roleStr);

            // Format authority with ROLE_ prefix for Spring Security hasRole() / hasAnyRole()
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role.name())
            );

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
