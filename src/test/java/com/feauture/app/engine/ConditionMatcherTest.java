package com.feauture.app.engine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.feauture.app.engine.Models.Condition;
import com.feauture.app.engine.Models.Operator;
import com.feauture.app.engine.Models.UserContext;

public class ConditionMatcherTest {
    private UserContext user(String key, String value) {
        return new UserContext("u1", Map.of(key, value));
    }

    @Test
    void inMatchesWhenValueIsInList() {
        Condition c = new Condition("country", Operator.IN, List.of("IN", "LK"));
        assertThat(ConditionMatcher.matches(c, user("country", "IN"))).isTrue();
        assertThat(ConditionMatcher.matches(c, user("country", "US"))).isFalse();
    }

    @Test
    void notInIsOppositeOfIn() {
        Condition c = new Condition("country", Operator.NOT_IN, List.of("IN", "LK"));
        assertThat(ConditionMatcher.matches(c, user("country", "US"))).isTrue();
        assertThat(ConditionMatcher.matches(c, user("country", "IN"))).isFalse();
    }

    @Test
    void equalsAndNotEquals() {
        Condition eq = new Condition("plan", Operator.EQUALS, List.of("pro"));
        Condition ne = new Condition("plan", Operator.NOT_EQUALS, List.of("pro"));
        assertThat(ConditionMatcher.matches(eq, user("plan", "pro"))).isTrue();
        assertThat(ConditionMatcher.matches(eq, user("plan", "free"))).isFalse();
        assertThat(ConditionMatcher.matches(ne, user("plan", "free"))).isTrue();
        assertThat(ConditionMatcher.matches(ne, user("plan", "pro"))).isFalse();
    }

    @Test
    void containsAndStartsWith() {
        Condition contains = new Condition("email", Operator.CONTAINS, List.of("@corp"));
        Condition starts = new Condition("email", Operator.STARTS_WITH, List.of("vicky"));
        assertThat(ConditionMatcher.matches(contains, user("email", "vicky@corp.com"))).isTrue();
        assertThat(ConditionMatcher.matches(starts, user("email", "vicky@corp.com"))).isTrue();
        assertThat(ConditionMatcher.matches(starts, user("email", "bob@corp.com"))).isFalse();
    }

    @Test
    void greaterAndLessThanCompareAsNumbers() {
        Condition gt = new Condition("age", Operator.GT, List.of("18"));
        Condition lt = new Condition("age", Operator.LT, List.of("18"));
        assertThat(ConditionMatcher.matches(gt, user("age", "25"))).isTrue();
        assertThat(ConditionMatcher.matches(gt, user("age", "9"))).isFalse();
        assertThat(ConditionMatcher.matches(lt, user("age", "9"))).isTrue();
    }

    @Test
    void nonNumericNeverMatchesGtOrLt() {
        Condition gt = new Condition("age", Operator.GT, List.of("18"));
        Condition lt = new Condition("age", Operator.LT, List.of("18"));
        assertThat(ConditionMatcher.matches(gt, user("age", "abc"))).isFalse();
        assertThat(ConditionMatcher.matches(lt, user("age", "abc"))).isFalse();
    }

    @Test
    void missingAttributeNeverMatches() {
        Condition in = new Condition("country", Operator.IN, List.of("IN"));
        Condition notEq = new Condition("country", Operator.NOT_EQUALS, List.of("IN"));
        UserContext empty = new UserContext("u1", Map.of());
        assertThat(ConditionMatcher.matches(in, empty)).isFalse();
        assertThat(ConditionMatcher.matches(notEq, empty)).isFalse();
    }

    @Test
    void emptyOrNullValuesNeverMatch() {
        Condition empty = new Condition("country", Operator.EQUALS, List.of());
        Condition nulls = new Condition("country", Operator.EQUALS, null);
        assertThat(ConditionMatcher.matches(empty, user("country", "IN"))).isFalse();
        assertThat(ConditionMatcher.matches(nulls, user("country", "IN"))).isFalse();
    }
}
