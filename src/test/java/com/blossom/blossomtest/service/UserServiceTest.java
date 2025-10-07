package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.user.Role;
import com.blossom.blossomtest.model.user.User;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("123456");
        user.setRole(Role.CUSTOMER);
    }

    // 🔹 Test: registro exitoso
    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Response> response = userService.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody().getMessage());
        assertEquals(Role.CUSTOMER, user.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 🔹 Test: email o password nulos
    @Test
    void testRegister_NullEmailOrPassword() {
        user.setEmail(null);

        ResponseEntity<Response> response = userService.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email and password cannot be empty.", response.getBody().getMessage());
        verify(userRepository, never()).save(any());
    }

    // 🔹 Test: usuario duplicado
    @Test
    void testRegister_ExistingEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseEntity<Response> response = userService.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists with the email.", response.getBody().getMessage());
        verify(userRepository, never()).save(any());
    }

    // 🔹 Test: login exitoso
    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        ResponseEntity<Response> response = userService.login("test@example.com", "123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful.", response.getBody().getMessage());
        assertNotNull(((Map<?, ?>) response.getBody().getData()).get("token"));
        verify(jwtUtil, times(1)).generateToken(any(User.class));
    }

    // 🔹 Test: login con email o password nulos
    @Test
    void testLogin_NullEmailOrPassword() {
        ResponseEntity<Response> response = userService.login(null, "123");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Must enter your email and password.", response.getBody().getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    // 🔹 Test: usuario no encontrado
    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        ResponseEntity<Response> response = userService.login("wrong@example.com", "123456");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody().getMessage());
    }

    // 🔹 Test: contraseña incorrecta
    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<Response> response = userService.login("test@example.com", "wrongpass");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Password incorrect.", response.getBody().getMessage());
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    // 🔹 Test: obtener perfil exitoso
    @Test
    void testGetProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(1L)).thenReturn(List.of());

        ResponseEntity<Response> response = userService.getProfile(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User profile obtained successfully.", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
        assertTrue(data.containsKey("user"));
        assertTrue(data.containsKey("orders"));
    }

    // 🔹 Test: perfil no encontrado
    @Test
    void testGetProfile_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = userService.getProfile(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody().getMessage());
    }
}
