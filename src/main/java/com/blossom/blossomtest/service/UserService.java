package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.iservice.IUserService;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.user.Role;
import com.blossom.blossomtest.model.user.User;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<Response> register(User user) {
        if ((user.getEmail() == null || user.getEmail().isEmpty()) || (user.getPassword() == null || user.getPassword().isEmpty())) {
            return ApiResponse.error("Email and password cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ApiResponse.error("User already exists with the email.", HttpStatus.CONFLICT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        user.setRole(user.getRole() == null ? Role.CUSTOMER : user.getRole());
        userRepository.save(user);

        return ApiResponse.success("User registered successfully.", user);
    }

    @Override
    public ResponseEntity<Response> login(String email, String password) {
        if (email == null || password == null) {
            return ApiResponse.error("Must enter your email and password.", HttpStatus.BAD_REQUEST);
        }


        var user = userRepository.findByEmail(email);
        if (user == null) {
            return ApiResponse.error("User not found.", HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error("Password incorrect.", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user);

        return ApiResponse.success("Login successful.", Map.of(
                "token", token,
                "userId", user.getId(),
                "role", user.getRole()
        ));
    }

    @Override
    public ResponseEntity<Response> getProfile(Long id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ApiResponse.error("User not found.", HttpStatus.NOT_FOUND);
        }

        var user = userOpt.get();
        var orders = orderRepository.findByUserId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("orders", orders);

        return ApiResponse.success("User profile obtained successfully.", data);
    }
}
