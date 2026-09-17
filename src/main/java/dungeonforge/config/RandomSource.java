package dungeonforge.config;

import java.util.List;
import java.util.Random;

public final class RandomSource {
    private static RandomSource instance;

    private Random rng;
    private long seed;

    private RandomSource() {
        this.seed = GameConfig.getInstance().getSeed();
        rng = new Random(this.seed);
    }

    public static synchronized RandomSource getInstance() {
        if( instance == null ) {
            instance = new RandomSource();
        }
        return instance;
    }

    public int nextInt(int bound) { return bound <= 0 ? 0 : rng.nextInt(bound); }

    public int between(int lo, int hi){ return hi <= lo ? lo : lo + rng.nextInt(hi - lo + 1); }

    public double nextDouble() { return rng.nextDouble(); }

    public <T> T pick(List<T> options) {
        return options.isEmpty() ? null : options.get(rng.nextInt(options.size()));
    }

    public <T> T pick(T[] options) {
        return options.length == 0 ? null : options[rng.nextInt(options.length)];
    }

    public long getSeed() { return seed; }

    // For dealing with US-1.2 AC #4
    public void reseed(long newSeed) {
        this.seed = newSeed;
        this.rng = new Random(newSeed);
    }

    // TEST Method ONLY
    public static void resetForTests() { instance = null; }

}