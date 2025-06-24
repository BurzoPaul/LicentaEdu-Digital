package com.utcn.edu_digital.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long BLOCK_TIME_MS = 5 * 60 * 1000; // 5 minute

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockTimes = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        int attemptsSoFar = attempts.getOrDefault(key, 0);
        attempts.put(key, attemptsSoFar + 1);
        if (attempts.get(key) >= MAX_ATTEMPT) {
            blockTimes.put(key, System.currentTimeMillis());
        }
    }

    public boolean isBlocked(String key) {
        if (!blockTimes.containsKey(key)) return false;

        long timePassed = System.currentTimeMillis() - blockTimes.get(key);
        if (timePassed > BLOCK_TIME_MS) {
            // Deblocare automată după timp
            blockTimes.remove(key);
            attempts.remove(key);
            return false;
        }
        return true;
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
        blockTimes.remove(key);
    }
}
