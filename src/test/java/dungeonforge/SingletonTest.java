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

public class SingletonTest {

    @BeforeEach
    void freshSingletons() {
    GameConfig.resetForTests();
    RandomSource.resetForTests();
    }

    // Tests for US-1.1
    @Test
    void configReturnsTheSameInstanceEveryTime() {
        assertSame(GameConfig.getInstance(), GameConfig.getInstance());
    }

    @Test
    void configConstructorIsPrivate() {
        Constructor<?>[] ctors = GameConfig.class.getDeclaredConstructors();
        assertEquals(1, ctors.length, "a singleton should expose exactly one constructor.");
        assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    }

    @Test
    void aPlayerIsBuiltFromConfiguredValues() {
        Player p = new Player("Testy");
        assertEquals(GameConfig.getInstance().getInt("playerStartingHp"), p.getMaxHp());
        assertEquals(GameConfig.getInstance().getInt("playerStartingAttack"), p.getAttackPower());
    }

    @Test
    void unknownKeysDoNotCrashTheGame() {
        assertEquals(0, GameConfig.getInstance().getInt("noSuchKey"));
    }

    // US-1.2

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

    @Test
    void theSameSeedProducesTheSameSeedSequence() {
        RandomSource.getInstance().reseed(12345L);
        int[] first = tenRolls();

        RandomSource.getInstance().reseed(12345L);
        int[] second = tenRolls();

        assertArrayEquals(first, second);
    }

    @Test
    void theDiffSeedProducesADiffSeedSequence() {
        RandomSource.getInstance().reseed(1L);
        int[] first = tenRolls();

        RandomSource.getInstance().reseed(2L);
        int[] second = tenRolls();

        assertFalse(java.util.Arrays.equals(first, second), "a different seed should produce a different seq");

    }

    @Test
    void theSameSeedProducesTheSameDungeon() {
        RandomSource.getInstance().reseed(999L);
        int monstersFirstRun = new GameWorld(new Player("First Run")).totalMonsters();

        RandomSource.getInstance().reseed(999L);
        int monstersSecondRun = new GameWorld(new Player("Second Run")).totalMonsters();

        assertEquals(monstersFirstRun, monstersSecondRun);
    }


    private int[] tenRolls() {
        int[] out = new int[10];
        for(int i=0; i < out.length; i++) {
            out[i] = RandomSource.getInstance().nextInt(1000);
        }
        return out;
    }

}