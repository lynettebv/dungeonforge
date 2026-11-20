package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.DungeonReport;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.ThemeKit;
import dungeonforge.factory.ThemeRegistry;
import dungeonforge.generate.CaveGenerator;
import dungeonforge.generate.CorridorGenerator;
import dungeonforge.generate.GeneratorRegistry;
import dungeonforge.generate.LevelGenerator;
import dungeonforge.generate.RingGenerator;
import dungeonforge.generate.SpokeGenerator;
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
                       new RingGenerator(), new SpokeGenerator());
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
    @Test
    void everyShapeKeepsAllTheRoomsItWasGiven() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertEquals(8, level.getRooms().size(),
                    shape.shapeName() + " threw rooms away; enforceReachability is a net, not a bin");
        }
    }

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

    // ================= US-8.2: primitives versus hooks =================

    @Test
    void theShapeStepIsAbstractSoItCannotBeForgotten() throws Exception {
        Method connect = LevelGenerator.class.getDeclaredMethod("connectRooms", DungeonLevel.class);
        assertTrue(Modifier.isAbstract(connect.getModifiers()));
    }

    /** A hook has a body, and the body does nothing. That is the whole difference. */
    @Test
    void hooksAreConcreteAndOptional() throws Exception {
        for (String hook : List.of("addShortcuts", "decorate")) {
            Method m = hook.equals("decorate")
                    ? LevelGenerator.class.getDeclaredMethod(hook, DungeonLevel.class, ThemeKit.class)
                    : LevelGenerator.class.getDeclaredMethod(hook, DungeonLevel.class);
            assertFalse(Modifier.isAbstract(m.getModifiers()), hook + " should be a hook, not a primitive");
        }
    }

    /** Three of four shapes decline both hooks, which is what "optional" means in practice. */
    @Test
    void onlyOneShapeOverridesAHookAndTheRestInheritTheSilence() {
        int overriders = 0;
        for (LevelGenerator shape : allShapes()) {
            try {
                shape.getClass().getDeclaredMethod("addShortcuts", DungeonLevel.class);
                overriders++;
            } catch (NoSuchMethodException ignored) { }
        }
        assertEquals(1, overriders, "only CaveGenerator should want extra edges");
    }

    /** The cave's hook actually runs, and the extra edges survive the invariant step. */
    @Test
    void theCaveHookAddsEdgesBeyondASpanningTree() {
        int treeEdges = 0, caveEdges = 0;
        for (int seed = 1; seed <= 8; seed++) {
            // DIFFERENT seeds, not resetForTests() eight times. The first version of this test
            // reset to the SAME seed on every pass, so it measured one dungeon eight times and
            // reported identical totals. A loop that does not vary its input is not a sample.
            RandomSource.getInstance().reseed(seed * 7919L);
            treeEdges += countDoors(new CorridorGenerator().generate(1, theme, 8, false));
            RandomSource.getInstance().reseed(seed * 7919L);
            caveEdges += countDoors(new CaveGenerator().generate(1, theme, 8, false));
        }
        assertTrue(caveEdges > treeEdges,
                "the cave should be loopier than a corridor; got " + caveEdges + " vs " + treeEdges);
    }

    private static int countDoors(DungeonLevel level) {
        int n = 0;
        for (Room r : level.getRooms()) n += r.getExits().size();
        return n;
    }

    // ================= US-8.3: shapes may only spend four directions =================

    @Test
    void noShapeUsesTheDescendingDirectionBecauseTheStairsOwnIt() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            for (Room r : level.getRooms()) {
                assertFalse(r.getExits().containsKey("down"),
                        shape.shapeName() + " spent the direction the staircase needs");
            }
        }
    }

    @Test
    void everyDoorLeadsBothWays() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            for (Room from : level.getRooms()) {
                for (var e : from.getExits().entrySet()) {
                    assertTrue(e.getValue().getExits().containsValue(from),
                            shape.shapeName() + ": one-way door out of " + from.getId());
                }
            }
        }
    }

    // ================= US-8.4: the fourth shape =================

    @Test
    void theRegistryHoldsOneEntryPerShape() {
        GeneratorRegistry registry = new GeneratorRegistry();
        assertEquals(4, registry.size());
        assertEquals(List.of("corridor", "cave", "ring", "spoke"), registry.shapeNames());
    }

    @Test
    void depthOneIsAlwaysACorridorSoTheGameOpensSimply() {
        assertEquals("corridor", new GeneratorRegistry().forDepth(1).shapeName());
    }

    @Test
    void theFourthShapeNeededNoChangeToTheOtherThree() {
        assertEquals("corridor", new CorridorGenerator().shapeName());
        assertEquals("cave", new CaveGenerator().shapeName());
        assertEquals("ring", new RingGenerator().shapeName());
    }

    // ================= the whole world, end to end =================

    @Test
    void aGeneratedWorldSatisfiesEveryInvariantOnEveryLevel() {
        GameWorld world = new GameWorld(new Player("Tester"));
        for (DungeonLevel level : world.getLevels()) {
            assertEquals(0, DungeonReport.countOrphans(level),
                    "level " + level.getDepth() + " has orphaned rooms");
        }
        assertTrue(world.startingRoom().getMonsters().isEmpty(),
                "the player must not materialise in a fight");
    }

    @Test
    void everyLevelButTheLastHasAReachableStaircase() {
        GameWorld world = new GameWorld(new Player("Tester"));
        List<DungeonLevel> levels = world.getLevels();
        for (int i = 0; i + 1 < levels.size(); i++) {
            assertTrue(DungeonReport.stairsReachable(levels.get(i)),
                    "level " + levels.get(i).getDepth() + ": cannot walk to the stairs down");
        }
    }

    @Test
    void theWorldStillHasMonstersAndLootAfterAllThatPruning() {
        GameWorld world = new GameWorld(new Player("Tester"));
        assertTrue(world.totalMonsters() > 0);
        assertTrue(world.totalLoot() > 0);
    }

    /** US-8.4's receipt, as an invariant rather than a claim: the seed-3 level is clean too. */
    @Test
    void theInvariantsHoldOnTheSeedThatExposedTheOldBugs() {
        RandomSource.getInstance().reseed(3L);
        GameWorld world = new GameWorld(new Player("Tester"));
        int orphans = 0, spawn = 0;
        for (DungeonLevel level : world.getLevels()) orphans += DungeonReport.countOrphans(level);
        spawn = world.startingRoom().getMonsters().size();
        assertEquals(0, orphans, "seed 3 produced 4 orphaned rooms before US-8.1");
        assertEquals(0, spawn, "seed 3 spawn-camped the player before US-8.1");
    }
}
