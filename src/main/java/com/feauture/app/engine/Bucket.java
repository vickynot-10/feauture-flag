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

}
