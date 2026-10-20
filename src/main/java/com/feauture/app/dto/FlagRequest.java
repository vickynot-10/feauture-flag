package com.feauture.app.dto;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FlagRequest {

        public record saveFlag(
                        @NotNull(message = "App is Required") Long app,
                        @NotBlank(message = "Key is Required") @Size(max = 50, message = "Maximum of 50 characters") @Pattern(regexp = "^[a-z0-9_-]+$", message = "Only lowercase letters, digits, underscore and dash") String flagKey,
                        @NotEmpty(message = "Definition is Required") Map<String, Object> definition) {
        }

        public record editFlag(
                        @NotNull(message = "Edit ID is Required") Long id,
                        @NotNull(message = "Expected version is Required") @Min(1) Integer expectedVersion,
                        @NotEmpty(message = "Definition is Required") Map<String, Object> definition) {
        }

        public record updateStatusFlag(
                        @NotNull(message = "Edit ID is Required") Long id) {
        }
}