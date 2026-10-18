package com.feauture.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ApiRequest {

        public record SaveApi(
                        @NotBlank(message = "Name is Required") @Size(max = 30, message = "Name must be max 30 characters") String name,
                        @NotNull(message = "App is Required") @Positive(message = "Invalid App") Long app_id) {
        }

        public record EditApi(
                        @NotBlank(message = "Name is Required") @Size(max = 30, message = "Name must be max 30 characters") String name,
                        @NotNull(message = "App is Required") @Positive(message = "Invalid App") Long app_id,
                        @NotNull(message = "ID is Required") @Positive(message = "Invalid ID") Long id) {
        }

        public record StatusUpdate(
                        @NotNull(message = "ID is Required") @Positive(message = "Invalid ID") Long id) {
        }

}
