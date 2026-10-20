package com.feauture.app.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import com.feauture.app.constants.AppConstants;
import com.feauture.app.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class AuthInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Cookie cookie = WebUtils.getCookie(request, AppConstants.COOKIE_NAME);
        if (cookie == null || cookie.getValue().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        }

        String userId = jwtService.verifyToken(cookie.getValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));

        request.setAttribute("userId", Long.valueOf(userId));
        return true;

    }
}
