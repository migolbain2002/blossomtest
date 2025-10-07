package com.blossom.blossomtest.config;

import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {

        String path = request.getRequestURI();
        if (path.startsWith("/h2-console") || path.startsWith("/users/login") || path.startsWith("/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            var userOpt = userRepository.findByEmail(email);
            if (userOpt != null && jwtUtil.isTokenValid(token, userOpt)) {
                var user = userOpt;
                var auth = new UsernamePasswordAuthenticationToken(
                        user, null, java.util.List.of(() -> "ROLE_" + user.getRole().name())
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
