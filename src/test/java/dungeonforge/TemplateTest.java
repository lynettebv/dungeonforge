package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.DungeonReport;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.ThemeKit;
import dungeonforge.factory.ThemeRegistry;
import dungeonforge.generate.CaveGenerator;
import dungeonforge.generate.CorridorGenerator;
import dungeonforge.generate.LevelGenerator;
import dungeonforge.generate.RingGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 10 -- Sprint 8. The Template Method pattern. */
class TemplateMethodTest {

    private ThemeKit theme;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
        theme = new ThemeRegistry(new MonsterFactory()).forDepth(1);
    }

    private static List<LevelGenerator> allShapes() {
        return List.of(new CorridorGenerator(), new CaveGenerator(),
                new RingGenerator());
    }

    // ================= US-8.1: the algorithm lives in one place =================

    /**
     * THE HEADLINE. Every shape satisfies every invariant, and no shape was asked to.
     *
     * In the starter this was false for two of three builders: the cave builder skipped the
     * reachability check and the ring builder populated the entrance. Those bugs were not fixed
     * -- the code that contained them stopped existing.
     */
    @Test
    void everyShapeProducesALevelWithNoOrphanedRooms() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertEquals(0, DungeonReport.countOrphans(level),
                    shape.shapeName() + " left rooms the entrance cannot reach");
        }
    }

    @Test
    void everyShapeLeavesTheEntranceEmpty() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertTrue(level.getRooms().get(0).getMonsters().isEmpty(),
                    shape.shapeName() + " spawned monsters where the player materialises");
        }
    }

    /** A well-written shape wastes nothing, so the invariant finds nothing to prune. */
    /* TODO: This will only be valid after US-8.3 refactor.
    @Test
    void everyShapeKeepsAllTheRoomsItWasGiven() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertEquals(8, level.getRooms().size(),
                    shape.shapeName() + " threw rooms away; enforceReachability is a net, not a bin");
        }
    }
    */

    @Test
    void everyShapeSetsFlavourOnTheEntrance() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertNotNull(level.getRooms().get(0).getFlavor(), shape.shapeName());
        }
    }

    /**
     * US-8.1 -- the structural guarantee, asserted rather than trusted.
     *
     * If generate() stops being final, a subclass can reorder or skip steps and every other test
     * in this class becomes a statement about today's subclasses rather than about the design.
     * Part D3 asks you to remove the `final` on purpose; this test is what tells you when you
     * have forgotten to put it back.
     */
    @Test
    void theTemplateMethodIsFinalSoNoSubclassCanReorderTheSteps() throws Exception {
        Method generate = LevelGenerator.class.getDeclaredMethod(
                "generate", int.class, ThemeKit.class, int.class, boolean.class);
        assertTrue(Modifier.isFinal(generate.getModifiers()),
                "generate() must be final -- that is the pattern, not a precaution");
    }

    /** The steps a subclass must not be able to skip are not visible to subclasses at all. */
    @Test
    void theInvariantStepIsPrivateToTheTemplate() {
        List<String> visible = new ArrayList<>();
        for (Method m : LevelGenerator.class.getDeclaredMethods()) {
            if (!Modifier.isPrivate(m.getModifiers())) visible.add(m.getName());
        }
        assertFalse(visible.contains("enforceReachability"),
                "a step a subclass can call is a step a subclass can call at the wrong time");
        assertFalse(visible.contains("populateRooms"));
        assertFalse(visible.contains("createRooms"));
    }
}
