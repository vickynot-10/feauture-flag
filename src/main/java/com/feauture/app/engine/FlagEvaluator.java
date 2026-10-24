package com.feauture.app.engine;

import java.util.List;
import com.feauture.app.engine.Models.EvaluationResult;
import com.feauture.app.engine.Models.Rule;
import com.feauture.app.engine.Models.Condition;
import com.feauture.app.engine.Models.FlagDefinition;
import com.feauture.app.engine.Models.Serve;
import com.feauture.app.engine.Models.Slice;
import com.feauture.app.engine.Models.UserContext;

public class FlagEvaluator {
    public static EvaluationResult evaluate(String flagKey, FlagDefinition def, UserContext ctx) {
        if (!def.enabled()) {
            return new EvaluationResult(def.offVariant(), "FLAG_DISABLED", null);
        }
        if (def.rules() != null) {
            for (Rule rule : def.rules()) {
                if (!allMatch(rule.conditions(), ctx)) {
                    continue;
                }
                EvaluationResult result = serve(flagKey, def, rule, ctx);
                if (result != null) {
                    return result;
                }
            }
        }
        return new EvaluationResult(def.defaultVariant(), "DEFAULT", null);
    }

    private static boolean allMatch(List<Condition> conditions, UserContext ctx) {
        if (conditions == null) {
            return true;
        }
        for (Condition c : conditions) {
            if (!ConditionMatcher.matches(c, ctx)) {
                return false;
            }
        }
        return true;
    }

    private static EvaluationResult serve(String flagKey, FlagDefinition def, Rule rule, UserContext ctx) {
        Serve serve = rule.serve();
        if (serve.variant() != null) {
            return new EvaluationResult(serve.variant(), "RULE_MATCH", rule.id());
        }
        String bucketValue = bucketValue(def, ctx);
        if (bucketValue == null || serve.rollout() == null) {
            return null;
        }
        int bucket = Bucket.bucket(flagKey, def.salt(), bucketValue);
        int cumulative = 0;
        for (Slice slice : serve.rollout()) {
            cumulative += slice.weight();
            if (bucket < cumulative) {
                return new EvaluationResult(slice.variant(), "ROLLOUT", rule.id());
            }
        }
        return null;
    }

    private static String bucketValue(FlagDefinition def, UserContext ctx) {
        if (def.bucketBy() == null || def.bucketBy().equals("user_id")) {
            return ctx.userId();
        }
        return ctx.get(def.bucketBy());
    }
}
