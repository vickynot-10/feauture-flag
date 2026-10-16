package com.feauture.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AuthRequest;
import com.feauture.app.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signUp(@Valid @RequestBody AuthRequest.SignUp request) {
        return authService.signUp(request);
    }

    @PostMapping("/sign-in")

    public ApiResponse<Void> signIn(@Valid @RequestBody AuthRequest.SignIn request) {

        return authService.signIn(request);
    }

}