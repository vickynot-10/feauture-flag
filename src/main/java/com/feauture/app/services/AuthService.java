package com.feauture.app.services;

import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final JwtService jwtService;

    public String signUp(AuthRequest.SignUp request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw ApiResponse.error(HttpStatus.CONFLICT, "Email already registered");
        }

        String username = request.name().trim();

        AppUser appuser = new AppUser();

        appuser.setEmail(email);
        appuser.setName(username);
        appuser.setPassword(passwordEncoder.encode(request.password()));

        AppUser savedUser = userRepository.save(appuser);

        String userID = String.valueOf(savedUser.getId());

        return jwtService.generateToken(userID);

    }

    public String signIn(AuthRequest.SignIn request) {
        String email = request.email().trim().toLowerCase();

        AppUser userData = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiResponse.error(HttpStatus.CONFLICT, "Invalid Credentails !"));

        String password = request.password();

        String hashedPassword = userData.getPassword();

        boolean isMatch = passwordEncoder.matches(password, hashedPassword);

        if (!isMatch) {
            throw ApiResponse.error(HttpStatus.CONFLICT, "Invalid Credentails");
        }

        String userID = String.valueOf(userData.getId());

        return jwtService.generateToken(userID);

    }

}
