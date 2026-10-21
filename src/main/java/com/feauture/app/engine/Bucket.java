package com.feauture.app.engine;

import java.nio.charset.StandardCharsets;

import com.feauture.app.constants.AppConstants;
import com.google.common.hash.Hashing;

public class Bucket {

    public static int bucket(String flagKey, String salt, String userId) {
        String input = flagKey + ":" + salt + ":" + userId;
        int hash = Hashing.murmur3_32_fixed().hashString(input, StandardCharsets.UTF_8).asInt();
        return Math.floorMod(hash, AppConstants.MAX_BUCKETS);
    }

    public static void main(String[] args) {
        System.out.println(bucket("new_checkout", "s1", "user-1"));
        System.out.println(bucket("new_checkout", "s1", "user-1"));
        System.out.println(bucket("new_checkout", "s1", "user-2"));
        System.out.println(bucket("dark_mode", "s1", "user-1"));
    }

}
