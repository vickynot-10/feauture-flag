package com.feauture.app.engine;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.feauture.app.constants.AppConstants;
import com.feauture.app.engine.Models.Condition;

import com.feauture.app.engine.Models.Rule;
import com.feauture.app.engine.Models.Slice;
import com.google.common.hash.Hashing;

public class FlagEvaluator {

    public boolean Evaluate(String flagKey, String salt, String bucketBy, Boolean defaultnOn,
            List<Rule> rules,
            Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return false;
        }

        first: for (int i = 0; i < rules.size(); i++) {
            Rule current_rule = rules.get(i);
            // Skip if current rule is null or id is blank || conditions array is empty
            if (shouldContinue(current_rule, attributes)) {
                continue;
            }

            for (int j = 0; j < current_rule.conditions().size(); j++) {
                Condition condition = current_rule.conditions().get(j);
                boolean matchCondition = ConditionMatch(condition, attributes);
                // break and go to next condition
                if (!matchCondition) {

                    continue first;
                }
            }
            // if all condions match then we gonna return what serve we saved

            // if null and rolllout is empty then false
            if (current_rule.serve().variant() == null) {

                List<Slice> rollouts = current_rule.serve().rollout();

                if (rollouts == null || rollouts.isEmpty()) {
                    return false;
                }

                String userId = attributes.get(bucketBy);

                if (userId == null || userId.isBlank()) {
                    return false;
                }

                int hashID = HashBucketID(flagKey, userId, salt);

                return RollOutUsers(rollouts, hashID);
            }

            return current_rule.serve().variant();
        }

        // if none of rule matches
        return defaultnOn;
    }

    private boolean shouldContinue(Rule rule, Map<String, String> attributes) {
        return rule == null || rule.id().isBlank() || rule.conditions().isEmpty();
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
