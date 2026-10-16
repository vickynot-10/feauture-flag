package com.feauture.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    public record SignUp(
            @NotBlank(message = "Name is Required") @Size(max = 100, min = 1, message = "Name must be 1 to 100 Characters") String name,

            @NotBlank(message = "Email is Required") @Email(message = "Enter a valid Email Address") @Size(max = 100, min = 1, message = "Email must be 1 to 100 Characters") String email,

            @NotBlank(message = "Password is required") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password) {

    }

    public record SignIn(
            @NotBlank(message = "Email is Required") @Email(message = "Enter a valid Email Address") @Size(max = 100, min = 1, message = "Email must be 1 to 100 Characters") String email,

            @NotBlank(message = "Password is required") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password) {

    }
}
