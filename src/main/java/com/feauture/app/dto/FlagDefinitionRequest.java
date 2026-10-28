package com.feauture.app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feauture.app.engine.Models.Operator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FlagDefinitionRequest {

        public record Definition(
                        @NotBlank(message = "Salt is required") @Size(max = 32) String salt,
                        @NotBlank(message = "Bucket by is required") @Size(max = 50) String bucketBy,
                        @NotNull(message = "Default is required") Boolean defaultOn,
                        @NotNull @Size(max = 50, message = "Maximum 50 rules") @Valid List<Rule> rules) {
        }

        public record Rule(
                        @NotBlank(message = "Rule id is required") String id,
                        @NotNull @Size(max = 20, message = "Maximum 20 conditions") @Valid List<Condition> conditions,
                        @NotNull(message = "Serve is required") @Valid Serve serve) {
        }

        public record Condition(
                        @NotBlank(message = "Attribute is required") @Size(max = 50) String attribute,
                        @NotNull(message = "Operator is required") Operator operator,
                        @NotEmpty(message = "Value is required") List<@NotBlank @Size(max = 100) String> values) {
        }

        public record Serve(
                        Boolean variant,
                        @Valid List<Slice> rollout) {

                @JsonIgnore
                @AssertTrue(message = "Serve either a fixed variant or a rollout")
                public boolean isExactlyOne() {
                        boolean fixed = variant != null;
                        boolean percent = rollout != null && !rollout.isEmpty();
                        return fixed != percent;
                }

                @JsonIgnore
                @AssertTrue(message = "Rollout weights must add up to 10000")
                public boolean isWeightsValid() {
                        if (rollout == null || rollout.isEmpty()) {
                                return true;
                        }
                        int sum = 0;
                        for (Slice s : rollout) {
                                if (s == null || s.weight() == null) {
                                        return false;
                                }
                                sum += s.weight();
                        }
                        return sum == 10000;
                }
        }

        public record Slice(
                        @NotNull Boolean variant,
                        @NotNull @Min(0) @Max(10000) Integer weight) {
        }
}