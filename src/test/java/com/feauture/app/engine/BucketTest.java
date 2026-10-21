package com.feauture.app.engine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BucketTest {

    @Test
    void sameInputGivesSameBucket() {
        int first = Bucket.bucket("new_checkout", "s1", "user-1");
        for (int i = 0; i < 100; i++) {
            assertThat(Bucket.bucket("new_checkout", "s1", "user-1")).isEqualTo(first);
        }
    }

    @Test
    void bucketIsAlwaysInRange() {
        for (int i = 0; i < 100000; i++) {
            int bucket = Bucket.bucket("new_checkout", "s1", "user-" + i);
            assertThat(bucket).isBetween(0, 9999);
        }
    }

    @Test
    void changingSaltChangesBuckets() {
        int changed = 0;
        for (int i = 0; i < 1000; i++) {
            int a = Bucket.bucket("new_checkout", "s1", "user-" + i);
            int b = Bucket.bucket("new_checkout", "s2", "user-" + i);
            if (a != b) {
                changed++;
            }
        }
        assertThat(changed).isGreaterThan(900);
    }

    @Test
    void differentFlagsGiveDifferentBuckets() {
        int changed = 0;
        for (int i = 0; i < 1000; i++) {
            int a = Bucket.bucket("flag_a", "s1", "user-" + i);
            int b = Bucket.bucket("flag_b", "s1", "user-" + i);
            if (a != b) {
                changed++;
            }
        }
        assertThat(changed).isGreaterThan(900);
    }

    @Test
    void twentyPercentCutoffSelectsAboutTwentyPercent() {
        int total = 100000;
        int selected = 0;
        for (int i = 0; i < total; i++) {
            if (Bucket.bucket("new_checkout", "s1", "user-" + i) < 2000) {
                selected++;
            }
        }
        assertThat(selected / (double) total).isBetween(0.195, 0.205);
    }
}