package com.example.quiz.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.quiz.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (jwtTokenProvider == null) {
            throw new ServletException("JwtTokenProvider bean is not available. Please ensure it is configured correctly.");
        }

        String jwt = getJwtFromRequest(request);
        logger.info("JWT received: {}", jwt);

        if (StringUtils.hasText(jwt)) {
            boolean isValid = jwtTokenProvider.validateToken(jwt);
            logger.info("Is JWT valid: {}", isValid);
            
            if (isValid) {
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = userService.findByUsername(username).orElse(null);
                
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Successfully set authentication for user: {}", username);
                } else {
                    logger.warn("UserDetails not found for username: {}", username);
                }
            } else {
                logger.warn("JWT validation failed for token: {}", jwt);
            }
        } else {
            logger.warn("JWT is empty or missing in the Authorization header");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
