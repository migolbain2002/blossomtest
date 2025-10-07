package com.blossom.blossomtest.iservice;

import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.user.User;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<Response> register(User user);
    ResponseEntity<Response> login(String email, String password);
    ResponseEntity<Response> getProfile(Long id);
}
