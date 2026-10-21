package com.feauture.app.engine;

import com.feauture.app.engine.Models.Condition;
import com.feauture.app.engine.Models.UserContext;

public final class ConditionMatcher {

    // new Condition("country", Operator.IN, List.of("IN", "LK"))

    // new UserContext("u1", Map.of("plan", "pro"));

    public static boolean matches(Condition condition, UserContext context) {

        String attribute = condition.attribute();
        String matchInUser = context.get(attribute);

        if (matchInUser == null || condition.values() == null
                || condition.values().isEmpty() || matchInUser.isEmpty()) {
            return false;
        }

        final String initialElement = condition.values().get(0);

        return switch (condition.operator()) {
            case EQUALS -> matchInUser.equals(initialElement);
            case NOT_EQUALS -> !matchInUser.equals(initialElement);
            case IN -> condition.values().contains(matchInUser);
            case NOT_IN -> !condition.values().contains(matchInUser);
            case CONTAINS -> matchInUser.contains(initialElement);
            case STARTS_WITH -> matchInUser.startsWith(initialElement);
            case GT -> compare(matchInUser, initialElement) > 0;
            case LT -> compare(matchInUser, initialElement) < 0;
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