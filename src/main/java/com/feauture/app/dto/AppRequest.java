package com.feauture.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AppRequest {

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

        public record SaveApp(
                        @NotBlank(message = "App Name is Required") @Size(max = 30, message = "App Name must be max 30 characters") String name,

                        @NotNull(message = "Environment is Required") @Min(value = 1, message = "Invalid Environment") @Max(value = 4, message = "Invalid Environment") Integer environment) {
        }

        public record EditApp(
                        @NotNull(message = "ID is Required") @Positive Long id,
                        @NotBlank(message = "App Name is Required") @Size(max = 30, message = "App Name must be max 30 characters") String name,
                        @NotNull(message = "Environment is Required") @Min(1) @Max(4) Integer environment) {
        }

        public record StatusUpdateApp(
                        @NotNull(message = "ID is Required") @Positive Long id) {
        }
}