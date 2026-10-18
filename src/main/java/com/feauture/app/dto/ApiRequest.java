package com.feauture.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ApiRequest {
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
