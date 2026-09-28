package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WEEK 3 -- US-1.3, "the one-instance rule is enforced, not hoped for."
 *
 * Note what these tests assert: SAME REFERENCE, not equal objects. For a Singleton those are
 * different claims and only the stronger one proves the constraint holds.
 */
class SingletonTest {

    @BeforeEach
    void freshSingletons() {
        // Because singletons are global state, one test can leak into the next. This is the
        // cost of the pattern, and these two lines are the price we pay for it every time.
        GameConfig.resetForTests();
        RandomSource.resetForTests();
    }

    // ---------- US-1.1 ----------

    @Test
    void configReturnsTheSameInstanceEveryTime() {
        assertSame(GameConfig.getInstance(), GameConfig.getInstance());
    }

    @Test
    void configConstructorIsPrivate() {
        Constructor<?>[] ctors = GameConfig.class.getDeclaredConstructors();
        assertEquals(1, ctors.length, "a singleton should expose exactly one constructor");
        assertTrue(Modifier.isPrivate(ctors[0].getModifiers()),
                "the constructor must be private, or `new GameConfig()` would compile");
    }

    @Test
    void configReadsValuesFromTheConfigFile() {
        assertEquals(80, GameConfig.getInstance().getInt("playerStartingHp"));
    }

    @Test
    void aPlayerIsBuiltFromConfiguredValues() {
        Player p = new Player("Tester");
        assertEquals(GameConfig.getInstance().getInt("playerStartingHp"), p.getMaxHp());
        assertEquals(GameConfig.getInstance().getInt("playerStartingAttack"), p.getAttackPower());
    }

    @Test
    void unknownKeysDoNotCrashTheGame() {
        assertEquals(0, GameConfig.getInstance().getInt("noSuchSetting"));
    }

    // ---------- US-1.2 ----------

    @Test
    void randomSourceReturnsTheSameInstanceEveryTime() {
        assertSame(RandomSource.getInstance(), RandomSource.getInstance());
    }

    @Test
    void randomSourceConstructorIsPrivate() {
        Constructor<?>[] ctors = RandomSource.class.getDeclaredConstructors();
        assertEquals(1, ctors.length, "a singleton should expose exactly one constructor.");
        assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    }

    /** AC1: the same seed produces the same sequence. */
    @Test
    void theSameSeedProducesTheSameSequence() {
        RandomSource.getInstance().reseed(12345L);
        int[] first = tenRolls();

        RandomSource.getInstance().reseed(12345L);
        int[] second = tenRolls();

        assertArrayEquals(first, second);
    }

    /**
     * AC4, and the criterion that matters most.
     *
     * Without this test, an implementation that ignores the seed entirely would still pass
     * the test above. The pair pins the behaviour down from both sides.
     */
    @Test
    void aDifferentSeedProducesADifferentSequence() {
        RandomSource.getInstance().reseed(1L);
        int[] first = tenRolls();

        RandomSource.getInstance().reseed(2L);
        int[] second = tenRolls();

        assertFalse(java.util.Arrays.equals(first, second),
                "a different seed should produce a different sequence");
    }

    /** The whole point, stated at the level a player would recognise. */
    @Test
    void theSameSeedProducesTheSameDungeon() {
        RandomSource.getInstance().reseed(999L);
        int monstersFirstRun = new GameWorld(new Player("A")).totalMonsters();

        RandomSource.getInstance().reseed(999L);
        int monstersSecondRun = new GameWorld(new Player("B")).totalMonsters();

        assertEquals(monstersFirstRun, monstersSecondRun);
    }

    private int[] tenRolls() {
        int[] out = new int[10];
        for (int i = 0; i < out.length; i++) out[i] = RandomSource.getInstance().nextInt(1000);
        return out;
    }
}

