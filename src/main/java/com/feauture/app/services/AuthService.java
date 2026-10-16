package com.feauture.app.services;

import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.feauture.app.dto.ApiResponse;
import com.feauture.app.dto.AuthRequest;
import com.feauture.app.entity.AppUser;
import com.feauture.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<Void> signUp(AuthRequest.SignUp request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String username = request.name().trim();

        AppUser appuser = new AppUser();

        appuser.setEmail(email);
        appuser.setName(username);
        appuser.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(appuser);

        return ApiResponse.success("User Created Successfully !", null);

    }

    public ApiResponse<Void> signIn(AuthRequest.SignIn request) {
        String email = request.email().trim().toLowerCase();

        AppUser userData = userRepository.findByEmail(email)
                .orElseThrow(this::invalidException);

        String password = request.password();

        String hashedPassword = userData.getPassword();

        boolean isMatch = passwordEncoder.matches(password, hashedPassword);

        if (!isMatch) {
            throw invalidException();
        }

        return ApiResponse.success("Logged In Successfully !", null);

    }

    private ResponseStatusException invalidException() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials !");
    }
}
