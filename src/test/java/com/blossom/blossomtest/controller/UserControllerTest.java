package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IUserService;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.user.User;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@WebMvcTest(UserController.class)
@TestPropertySource(properties = "controller.properties.base-path=/")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        Response mockResponse = new Response(200, "User registered successfully.", mockUser);
        when(userService.register(any(User.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User registered successfully."));
    }

    @Test
    void testLogin_Success() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("password", "password123");

        Response mockResponse = new Response(200, "Login successful.", null);
        when(userService.login(eq("test@example.com"), eq("password123")))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(get("/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Login successful."));
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        Long id = 1L;
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setEmail("user@example.com");

        Response mockResponse = new Response(200, "User profile retrieved successfully.", mockUser);
        when(userService.getProfile(id))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User profile retrieved successfully."));
    }
}
