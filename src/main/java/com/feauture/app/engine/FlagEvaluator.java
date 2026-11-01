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
import com.feauture.app.engine.Models.EvaluateResultObject;
import com.feauture.app.engine.Models.EvaluationResult;
import com.google.common.hash.Hashing;

@Component
public class FlagEvaluator {

    public EvaluationResult Evaluate(String flagKey, String salt, String bucketValue, Boolean defaultnOn,
            List<Rule> rules,
            Map<String, String> attributes) {
        List<EvaluateResultObject> trace = new ArrayList<>();

        if (attributes == null || attributes.isEmpty()) {
            trace.add(new EvaluateResultObject(AppConstants.FAILED, null, "Attributes are empty"));
            return new EvaluationResult(false, trace);
        }

        first: for (int i = 0; i < rules.size(); i++) {
            Rule current_rule = rules.get(i);

            if (shouldContinue(current_rule)) {
                String skippedId = current_rule == null ? null : current_rule.id();
                trace.add(new EvaluateResultObject(AppConstants.SKIPPED, skippedId, skipReason(current_rule)));
                continue;
            }

            for (int j = 0; j < current_rule.conditions().size(); j++) {
                Condition condition = current_rule.conditions().get(j);

                if (!ConditionMatch(condition, attributes)) {
                    trace.add(new EvaluateResultObject(
                            AppConstants.FAILED,
                            current_rule.id(),
                            String.format("Condition not matched: %s %s %s",
                                    condition.attribute(), condition.operator(), condition.values())));
                    continue first;
                }
            }

            if (current_rule.serve().variant() == null) {
                List<Slice> rollouts = current_rule.serve().rollout();

                if (rollouts == null || rollouts.isEmpty()) {
                    trace.add(new EvaluateResultObject(
                            AppConstants.FAILED,
                            current_rule.id(),
                            "Conditions matched but rollout is empty"));
                    return new EvaluationResult(false, trace);
                }

                int hashID = HashBucketID(flagKey, bucketValue, salt);
                boolean served = RollOutUsers(rollouts, hashID);

                trace.add(new EvaluateResultObject(
                        AppConstants.SUCCEED,
                        current_rule.id(),
                        String.format("All conditions matched, rollout bucket %d served %s", hashID, served)));
                return new EvaluationResult(served, trace);
            }

            boolean served = current_rule.serve().variant();

            trace.add(new EvaluateResultObject(
                    AppConstants.SUCCEED,
                    current_rule.id(),
                    String.format("All conditions matched, served %s", served)));
            return new EvaluationResult(served, trace);
        }

        trace.add(new EvaluateResultObject(
                AppConstants.SUCCEED,
                null,
                String.format("No rule matched, returned default %s", defaultnOn)));
        return new EvaluationResult(defaultnOn, trace);
    }

    private boolean shouldContinue(Rule rule) {
        return rule == null
                || rule.id() == null
                || rule.id().isBlank()
                || rule.conditions() == null
                || rule.conditions().isEmpty();
    }

    private String skipReason(Rule rule) {
        if (rule == null) {
            return "Rule is null";
        }
        if (rule.id() == null || rule.id().isBlank()) {
            return "Rule id is blank";
        }
        return String.format("Conditions are empty on %s", rule.id());
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

        if (attribute == null || attribute.isEmpty() || matchInAttribute == null || matchInAttribute.isEmpty()) {
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