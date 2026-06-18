package main;

import arc.struct.ObjectMap;
import arc.util.Time;
import java.util.Iterator;

public class RateLimiter {
    private static final ObjectMap<String, Long> connectionAttempts = new ObjectMap<>();
    private static final Integer ConnectionCooldown = 2000;
    private static final Integer ConnectionLifetime = 60000;

    public static boolean ConLimited(String ip) {
        cleanupOldEntries(connectionAttempts);
        long now = Time.millis();
        long lastAttempt = connectionAttempts.get(ip, 0L);
        if (now - lastAttempt < ConnectionCooldown) {
            return true;
        }
        return false;
    }

    private static void cleanupOldEntries(ObjectMap<String, Long> map) {
        long now = Time.millis();
        Iterator<String> iter = map.keys().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (now - map.get(key) > ConnectionLifetime) {
                iter.remove();
            }
        }
    }
}
