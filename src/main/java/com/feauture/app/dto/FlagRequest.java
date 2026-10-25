package com.feauture.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FlagRequest {

        public record PageQuery(
                        @Min(value = 1, message = "Minimum Page is 1") Integer page,
                        @Min(value = 1, message = "Minimum Limit is 1") @Max(value = 100, message = "Maximum Limit is 100") Integer limit,
                        @Size(max = 30, message = "Search must be max 30 characters") String search) {

                public PageQuery {
                        page = page == null ? 1 : page;
                        limit = limit == null ? 10 : limit;
                        search = search == null || search.isBlank() ? null : search.trim();
                }
        }

        public record saveFlag(
                        @NotNull(message = "App is Required") Long app,
                        @NotBlank(message = "Name is Required") @Size(max = 30, message = "Name must be max 30 characters") String name,
                        @NotBlank(message = "Key is Required") @Size(max = 50, message = "Maximum of 50 characters") @Pattern(regexp = "^[a-z0-9_-]+$", message = "Only lowercase letters, digits, underscore and dash") String flagKey) {
        }

        public record editFlag(
                        @NotNull(message = "Edit ID is Required") Long id,
                        @NotBlank(message = "Name is Required") @Size(max = 30, message = "Name must be max 30 characters") String name,
                        @NotNull(message = "Expected version is Required") @Min(1) Integer expectedVersion) {
        }

        public record idFlagRequest(
                        @NotNull(message = "Edit ID is Required") Long id) {
        }

}