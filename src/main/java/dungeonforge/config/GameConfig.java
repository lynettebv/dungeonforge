package dungeonforge.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class GameConfig {

    private static GameConfig instance;
    private final Map<String, Object> settings = new LinkedHashMap<>();

    private GameConfig() {
        loadDefaults();
        loadFromClasspath("config.json");
    }

    public static synchronized GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    private void loadDefaults() {
        settings.put("playerStartingHp", 80.0);
        settings.put("playerStartingAttack", 8.0);
        settings.put("playerStartingDefense", 2.0);
        settings.put("carryCapacity", 60.0);
        settings.put("dungeonDepth", 3.0);
        settings.put("roomsPerLevel", 8.0);
        settings.put("maxMonstersPerRoom", 2.0);
        settings.put("seed", 20260828.0);

    }

    private void loadFromClasspath(String resourceName) {
        String text = readResource(resourceName);
        if (text == null) return;
        try  {
            settings.putAll(Json.parseObject(text));
        } catch (RuntimeException e) {
            System.err.println("[config] could not read " + resourceName + ": using defaults.");
        }
    }

    public static String readResource(String resourceName) {
        try (InputStream in = GameConfig.class.getResourceAsStream("/data/" + resourceName)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            System.err.println("[config] could not read " + resourceName + ": using defaults.");
        return null;
        }
    }

    public int getInt(String key) {
        Object value = settings.get(key);
        return value instanceof Number ? ((Number)value).intValue() : 0;
    }

    public double getDouble(String key) {
        Object value = settings.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    public long getSeed() {
        return (long)getDouble("seed");
    }

    // TEST Method ONLY
    public static void resetForTests() { instance = null; }

}
