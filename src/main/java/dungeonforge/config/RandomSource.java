package dungeonforge.config;

import java.util.List;
import java.util.Random;

/**
 * WEEK 3 -- SINGLETON.  (US-1.2)
 *
 * This is the singleton with the STRONGEST justification in the whole project, and it is the
 * one to point at when someone says Singleton is always an anti-pattern.
 *
 * One RNG plus one seed means the same dungeon every run. That gives you:
 *   - reproducible bug reports  ("seed 12345, level 2, the boss room is empty")
 *   - deterministic tests       (a test that generates a level gets the same level every time)
 *   - a save file that stores a SEED instead of a whole dungeon layout  (you will use this in Week 12)
 *
 * Two RNGs would silently destroy all three. That is a correctness requirement, not a
 * convenience -- which is exactly the bar a Singleton has to clear.
 */
public final class RandomSource {

    private static RandomSource instance;

    private Random rng;
    private long seed;

    private RandomSource() {
        this.seed = GameConfig.getInstance().getSeed();
        this.rng = new Random(seed);
    }

    public static synchronized RandomSource getInstance() {
        if (instance == null) {
            instance = new RandomSource();
        }
        return instance;
    }

    public int nextInt(int bound)      { return bound <= 0 ? 0 : rng.nextInt(bound); }
    public int between(int lo, int hi) { return hi <= lo ? lo : lo + rng.nextInt(hi - lo + 1); }
    public double nextDouble()         { return rng.nextDouble(); }
    public <T> T pick(List<T> options) { return options.isEmpty() ? null : options.get(rng.nextInt(options.size())); }
    public <T> T pick(T[] options)     { return options.length == 0 ? null : options[rng.nextInt(options.length)]; }

    public long getSeed() { return seed; }

    /** Lets a test -- or a --seed flag -- ask for a specific world. AC4 of US-1.2. */
    public void reseed(long newSeed) {
        this.seed = newSeed;
        this.rng = new Random(newSeed);
    }

    /** TEST HOOK ONLY. Same caveat as GameConfig.resetForTests(). */
    public static void resetForTests() { instance = null; }
}
