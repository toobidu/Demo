package org.example.demo.Service.Interface;

import jakarta.validation.Valid;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Authentication.LoginRequest;
import org.example.demo.Modal.DTO.Authentication.LoginResponse;
import org.example.demo.Modal.DTO.Authentication.RegisterRequest;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IAuthService {
    ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest);
    ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest);
    ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> request);
}
