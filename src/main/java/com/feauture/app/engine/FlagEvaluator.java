package com.feauture.app.engine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.feauture.app.constants.AppConstants;
import com.feauture.app.dto.FlagDefinitionRequest.Condition;
import com.feauture.app.dto.FlagDefinitionRequest.Rule;
import com.feauture.app.dto.FlagDefinitionRequest.Slice;
import com.feauture.app.engine.Models.EvaluationResult;
import com.feauture.app.engine.Models.EvaluationResult.FailedCondition;
import com.feauture.app.engine.Models.EvaluationResult.MatchedCondition;
import com.feauture.app.engine.Models.EvaluationResult.Reason;
import com.feauture.app.engine.Models.EvaluationResult.RuleStatus;
import com.feauture.app.engine.Models.EvaluationResult.RuleTrace;
import com.google.common.hash.Hashing;

@Component
public class FlagEvaluator {

    public EvaluationResult Evaluate(String flagKey, String salt, String bucketValue, Boolean defaultnOn,
            List<Rule> rules,
            Map<String, String> attributes) {
        List<RuleTrace> trace = new ArrayList<>();

        if (attributes == null || attributes.isEmpty()) {
            addNotEvaluated(trace, rules, 0);
            return new EvaluationResult(false, Reason.NO_ATTRIBUTES, null, List.of(), null, trace);
        }

        first: for (int i = 0; i < rules.size(); i++) {
            Rule current_rule = rules.get(i);

            String invalidNote = invalidReason(current_rule);
            if (invalidNote != null) {
                trace.add(new RuleTrace(i, ruleIdOf(current_rule), RuleStatus.SKIPPED_INVALID, invalidNote, null));
                continue;
            }

            List<MatchedCondition> matched = new ArrayList<>();

            for (int j = 0; j < current_rule.conditions().size(); j++) {
                Condition condition = current_rule.conditions().get(j);

                if (!ConditionMatch(condition, attributes)) {
                    trace.add(new RuleTrace(i, current_rule.id(), RuleStatus.NO_MATCH, null,
                            new FailedCondition(
                                    condition.attribute(),
                                    condition.operator().name(),
                                    condition.values(),
                                    attributes.get(condition.attribute()))));
                    continue first;
                }

                matched.add(new MatchedCondition(
                        condition.attribute(),
                        condition.operator().name(),
                        condition.values(),
                        attributes.get(condition.attribute())));
            }

            trace.add(new RuleTrace(i, current_rule.id(), RuleStatus.MATCHED, null, null));
            addNotEvaluated(trace, rules, i + 1);

            Boolean variant = current_rule.serve().variant();

            if (variant != null) {
                return new EvaluationResult(variant, Reason.RULE_MATCH, current_rule.id(), matched, null, trace);
            }

            List<Slice> rollouts = current_rule.serve().rollout();

            if (rollouts == null || rollouts.isEmpty()) {
                return new EvaluationResult(false, Reason.ROLLOUT_EMPTY, current_rule.id(), matched, null, trace);
            }

            int hashID = HashBucketID(flagKey, bucketValue, salt);

            return new EvaluationResult(RollOutUsers(rollouts, hashID), Reason.ROLLOUT, current_rule.id(), matched,
                    hashID, trace);
        }

        return new EvaluationResult(defaultnOn, Reason.DEFAULT, null, List.of(), null, trace);
    }

    private void addNotEvaluated(List<RuleTrace> trace, List<Rule> rules, int start) {
        for (int k = start; k < rules.size(); k++) {
            trace.add(new RuleTrace(k, ruleIdOf(rules.get(k)), RuleStatus.NOT_EVALUATED, null, null));
        }
    }

    private String ruleIdOf(Rule rule) {
        return rule == null ? null : rule.id();
    }

    private String invalidReason(Rule rule) {
        if (rule == null) {
            return "Rule is empty";
        }
        if (rule.id() == null || rule.id().isBlank()) {
            return "Rule has no id";
        }
        if (rule.conditions() == null || rule.conditions().isEmpty()) {
            return "Rule has no conditions";
        }
        return null;
    }

    private boolean RollOutUsers(List<Slice> rollouts, int hashID) {
        int cumulativeWeight = 0;

        for (Slice rollout : rollouts) {
            cumulativeWeight += rollout.weight();

            if (hashID < cumulativeWeight) {
                return rollout.variant();
            }
        }

        throw new IllegalArgumentException("Invalid rollout weights");
    }

    private int HashBucketID(String flagKey, String id, String salt) {
        String input = flagKey + ":" + salt + ":" + id;
        int hash = Hashing.murmur3_32_fixed().hashString(input, StandardCharsets.UTF_8).asInt();
        return Math.floorMod(hash, AppConstants.MAX_BUCKETS);
    }

    private boolean ConditionMatch(Condition condition, Map<String, String> attributes) {
        String attribute = condition.attribute();
        String matchInAttribute = attributes.get(attribute);

        if (attribute == null || attribute.isEmpty() || matchInAttribute == null || matchInAttribute.isEmpty()
                || condition.values() == null || condition.values().isEmpty()) {
            return false;
        }

        final String initialElement = condition.values().get(0);

        return switch (condition.operator()) {
            case EQUALS -> matchInAttribute.equals(initialElement);
            case NOT_EQUALS -> !matchInAttribute.equals(initialElement);
            case IN -> condition.values().contains(matchInAttribute);
            case NOT_IN -> !condition.values().contains(matchInAttribute);
            case CONTAINS -> matchInAttribute.contains(initialElement);
            case STARTS_WITH -> matchInAttribute.startsWith(initialElement);
            case GT -> compare(matchInAttribute, initialElement) > 0;
            case LT -> compare(matchInAttribute, initialElement) < 0;
            default -> false;
        };
    }

    private static int compare(String actual, String expected) {
        try {
            return Double.compare(Double.parseDouble(actual), Double.parseDouble(expected));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}