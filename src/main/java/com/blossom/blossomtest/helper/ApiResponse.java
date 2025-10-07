package com.blossom.blossomtest.helper;

import com.blossom.blossomtest.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

    public static ResponseEntity<Response> success(String message, Object data) {
        Response response = new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Response> error(String message, HttpStatus status) {
        Response response = new Response();
        response.setCode(status.value());
        response.setMessage(message);
        response.setData(null);
        return ResponseEntity.status(status).body(response);
    }
}
