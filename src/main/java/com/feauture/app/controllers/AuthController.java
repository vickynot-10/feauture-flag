package com.feauture.app.controllers;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.feauture.app.constants.AppConstants;
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
        public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody AuthRequest.SignUp request) {

                String token = authService.signUp(request);
                ResponseCookie cookie = ResponseCookie.from(AppConstants.COOKIE_NAME, token).httpOnly(true)
                                .secure(AppConstants.IN_PRODUCTION).path("/")
                                .maxAge(24 * 60 * 60).sameSite("Lax").build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.success("Signed Up Successfully !", null));

        }

        @PostMapping("/sign-in")

        public ResponseEntity<ApiResponse<Void>> signIn(@Valid @RequestBody AuthRequest.SignIn request) {

                String token = authService.signIn(request);

                ResponseCookie cookie = ResponseCookie.from(AppConstants.COOKIE_NAME, token).httpOnly(true)
                                .secure(AppConstants.IN_PRODUCTION).path("/")
                                .maxAge(24 * 60 * 60).sameSite("Lax").build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.success("Logged In Successfully !", null));
        }

        @PostMapping("/sign-out")
        public ResponseEntity<ApiResponse<Void>> signOut() {

                ResponseCookie cookie = ResponseCookie.from(AppConstants.COOKIE_NAME, "")
                                .httpOnly(true)
                                .secure(AppConstants.IN_PRODUCTION)
                                .path("/")
                                .maxAge(0)
                                .sameSite("Lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(ApiResponse.success("Logged Out Successfully!", null));
        }

}