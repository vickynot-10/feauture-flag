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

    // new UserContext("u1", Map.of("country", "in"));
    public record UserContext(String id, Map<String, String> attributes) {

        public String get(String name) {
            if (attributes == null) {
                return null;
            }
            return attributes.get(name);
        }
    }

    public record EvaluationResult(
            boolean value,
            Reason reason,
            String ruleId,
            List<MatchedCondition> matchedConditions,
            Integer bucket,
            List<RuleTrace> trace) {

        public enum Reason {
            NO_ATTRIBUTES,
            RULE_MATCH,
            ROLLOUT,
            ROLLOUT_EMPTY,
            DEFAULT
        }

        public enum RuleStatus {
            MATCHED,
            NO_MATCH,
            SKIPPED_INVALID,
            NOT_EVALUATED
        }

        public record MatchedCondition(
                String attribute,
                String operator,
                List<String> values,
                String actual) {
        }

        public record FailedCondition(
                String attribute,
                String operator,
                List<String> values,
                String actual) {
        }

        public record RuleTrace(
                int index,
                String ruleId,
                RuleStatus status,
                String note,
                FailedCondition failedCondition) {
        }
    }
}
