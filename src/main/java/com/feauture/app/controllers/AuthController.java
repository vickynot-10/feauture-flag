package com.feauture.app.controllers;

import com.feauture.app.dto.AuthRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @PostMapping("/sign-up")
    public String SignUp(@Valid @RequestBody AuthRequest.SignUp request) {
        // String name = request.name();
        return " Sign Up ";
    }

}
