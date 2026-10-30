package com.feauture.app.engine;

import java.util.Map;
import java.util.List;

public class Models {
    public enum Operator {
        EQUALS, NOT_EQUALS, IN, NOT_IN, CONTAINS, STARTS_WITH, GT, LT
    }

    // new Condition("country", Operator.IN, List.of("IN", "LK"))
    public record Condition(String attribute, Operator operator, List<String> values) {
    }

    public record Slice(boolean variant, int weight) {
    }

    public record Serve(Boolean variant, List<Slice> rollout) {
    }

    public record Rule(String id, List<Condition> conditions, Serve serve) {
    }

    public record FlagDefinition(String salt, String bucketBy, boolean defaultOn, List<Rule> rules) {
    }

    public record EvaluationResult(boolean on, String reason, String ruleId, Integer bucket) {
    }

    // new UserContext("u1", Map.of("country", "in"));
    public record UserContext(String id, Map<String, String> attributes) {

        public String get(String name) {
            if (attributes == null) {
                return null;
            }
            return attributes.get(name);
        }
    }
}
