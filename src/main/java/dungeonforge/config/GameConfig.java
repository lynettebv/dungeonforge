package dungeonforge.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WEEK 3 -- SINGLETON.  (US-1.1)
 *
 * Exactly one settings source for the whole game. The PRIVATE CONSTRUCTOR plus the STATIC
 * ACCESSOR is what enforces that; a public static field would only ask nicely.
 *
 * Why this deserves to be a Singleton -- the honest version:
 *   It is not "because settings are used everywhere." Lots of things are used everywhere and
 *   should still be passed as parameters. It is because a SECOND GameConfig would be a
 *   genuine bug: two parts of the game disagreeing about how much HP a player starts with is
 *   not a preference, it is a defect. The class enforces a rule that must not be broken.
 *
 * Honest cost, and you should be able to state it:
 *   This introduces global state. It makes testing harder -- see resetForTests() below,
 *   which exists ONLY because the pattern made the class hard to test. That method is a
 *   smell, and it is the price of the pattern.
 *
 * This project permits exactly TWO singletons: this one and RandomSource. If you can pass a
 * thing in as a parameter, it is not a Singleton -- it is a dependency.
 */
public final class GameConfig {

    private static GameConfig instance;

    private final Map<String, Object> settings = new LinkedHashMap<>();

    /** PRIVATE. This is the line that makes the pattern work. */
    private GameConfig() {
        loadDefaults();
        loadFromClasspath("config.json");
    }

    /**
     * Lazy initialisation: the instance is not built until somebody asks.
     * `synchronized` makes this safe if the game ever becomes multi-threaded. Our game is
     * single-threaded, so this is insurance rather than necessity -- see the Week 3 notes on
     * why we did NOT use double-checked locking.
     */
    public static synchronized GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    /** AC4: documented defaults, so a missing or unreadable config file cannot stop the game. */
    private void loadDefaults() {
        settings.put("playerStartingHp", 60.0);
        settings.put("playerStartingAttack", 8.0);
        settings.put("playerStartingDefense", 2.0);
        settings.put("carryCapacity", 60.0);
        settings.put("dungeonDepth", 3.0);
        settings.put("roomsPerLevel", 8.0);
        settings.put("maxMonstersPerRoom", 2.0);
        settings.put("restHealPerRoom", 8.0);
        settings.put("fleeThreshold", 0.30);
        settings.put("seed", 20260818.0);
    }

    /** AC1 and AC4: overlay whatever the file provides; keep defaults for anything missing. */
    private void loadFromClasspath(String resourceName) {
        String text = readResource(resourceName);
        if (text == null) return;                         // no file -> defaults stand
        try {
            settings.putAll(Json.parseObject(text));
        } catch (RuntimeException e) {
            System.err.println("[config] " + resourceName + " is malformed; using defaults.");
        }
    }

    /**
     * WEEK 4 -- made public so the MonsterFactory can load monsters.json the same way.
     * Reading a classpath resource is a utility, not a configuration concern, and this is the
     * smallest honest place to put it for now.
     */
    public static String readResource(String resourceName) {
        try (InputStream in = GameConfig.class.getResourceAsStream("/data/" + resourceName)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[config] could not read " + resourceName);
            return null;
        }
    }

    public int getInt(String key) {
        Object v = settings.get(key);
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public double getDouble(String key) {
        Object v = settings.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : 0;
    }

    public long getSeed() { return (long) getDouble("seed"); }

    /**
     * TEST HOOK ONLY. Never call this from game code.
     *
     * Be honest about what this is: the Singleton made this class hard to test, because a
     * test that changes a setting would leak into the next test. This method exists to undo
     * that. A design that needs a special method just so it can be tested is telling you
     * something, and Week 12's Facade will start to offer alternatives.
     */
    public static void resetForTests() { instance = null; }
}
