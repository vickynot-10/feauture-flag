package com.feauture.app.engine;

import java.util.Map;
import java.util.List;

public class Models {
    public enum Operator {
        EQUALS, NOT_EQUALS, IN, NOT_IN, CONTAINS, STARTS_WITH, GT, LT
    }

    public record Condition(String attribute, Operator operator, List<String> values) {
    }

    public record Slice(String variant, int weight) {
    }

    public record Serve(String variant, List<Slice> rollout) {
    }

    public record Rule(String id, List<Condition> conditions, Serve serve) {
    }

    public record FlagDefinition(
            boolean enabled,
            String salt,
            String bucketBy,
            List<Rule> rules,
            String defaultVariant,
            String offVariant) {
    }

    public record EvaluationResult(String variant, String reason, String ruleId) {
    }

    public record UserContext(String userId, Map<String, String> attributes) {

        public String get(String name) {
            if (attributes == null) {
                return null;
            }
            return attributes.get(name);
        }
    }
}
