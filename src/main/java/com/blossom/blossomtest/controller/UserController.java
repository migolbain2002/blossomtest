package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IUserService;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("${controller.properties.base-path}")
@Tag(name = "User Controller", description = "Users management")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    IUserService userService;

    @PostMapping("users/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<Response> registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("users/login")
    @Operation(summary = "Login with JWT")
    public ResponseEntity<Response> login(@RequestBody Map<String, String> credentials){
        return userService.login(credentials.get("email"), credentials.get("password"));
    }
    @GetMapping("users/{id}")
    public ResponseEntity<Response> getUserProfile(@PathVariable Long id) {
        return userService.getProfile(id);
    }
}
