package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.*;
import dungeonforge.factory.*;
import dungeonforge.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FactoryTest {
    private MonsterFactory factory;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();;
        RandomSource.resetForTests();
        factory = new MonsterFactory();
    }

    // US-2.1: Simple Factory
    @Test
    void everyBlueprintInTheDataFileIsRegistered() {
        assertEquals(19, factory.blueprintCount());
        assertTrue(factory.has("skeleton"));
        assertTrue(factory.has("rime_tyrant"));
    }

    @Test
    void theFactoryBuildsAMonsterFromABlueprint() {
        Monster m = factory.create("skeleton", 1 );
        assertEquals("Skeleton", m.getName());
        assertTrue(m.getMaxHp() > 0);
    }

    @Test
    void anUnknownIdProducesAFallbackRatherThanAnException() {
        Monster m = assertDoesNotThrow(() -> factory.create("my_nonexistent_monster", 1));
        assertNotNull(m);
        assertTrue(m.getMaxHp() > 0);
    }

    @Test
    void monstersScaleWithDepth() {
        RandomSource.getInstance().reseed(42L);
        Monster shallow = factory.create("skeleton", 1);
        RandomSource.getInstance().reseed(42L);
        Monster deep = factory.create("skeleton", 5);
        assertTrue(deep.getMaxHp() > shallow.getMaxHp(),
                "a level-5 skeleton should be tougher than a level-1 skeleton.");
    }

    @Test
    void bossesAreExcludedFromTheOrdinarySpawnPool() {
        assertFalse(factory.idsForTheme("crypt").contains("bone_tyrant"));
        assertEquals("bone_tyrant", factory.bossIdFromTheme("crypt"));
    }

    // Tests for US-2.2
    @Test
    void aThemeKitOnlyEverProducesItsOwnFamily() {
        ThemeKit crypt = new CryptThemeKit(factory);
        Set<String> cryptNames = Set.of("Skeleton", "Crypt Rat", "Wight", "Bone Priest", "Ghoul");
        for(int i=0; i < 50; i++) {
            assertTrue(cryptNames.contains(crypt.createMonster(1).getName()),
                    "a CryptThemeKit must never hand back a non-crypt monster");
        }
    }

    @Test
    void differentKitsProduceDifferentFamilies() {
        ThemeKit crypt = new CryptThemeKit(factory);
        ThemeKit forge = new ForgeThemeKit(factory);

        assertEquals("Bone Tyrant", crypt.createBoss(3).getName());
        assertEquals("Forge Tyrant", forge.createBoss(3).getName());
        assertNotEquals(crypt.themeName(), forge.themeName());
    }

    @Test
    void aKitProducesLootAndProseAsWellAsMonsters() {
        ThemeKit forge = new ForgeThemeKit(factory);
        Item loot = forge.createLoot(1);
        assertNotNull(loot);
        assertTrue(loot.getValue() > 0);
        assertFalse(forge.createRoomFlavor().isBlank());
    }

    @Test
    void everyRegisteredThemeIsReachableByDepth() {
        ThemeRegistry registry = new ThemeRegistry(factory);
        assertEquals(4, registry.size());

        assertEquals("Crypt", registry.forDepth(1).themeName());
        assertEquals("Forge", registry.forDepth(2).themeName());
        assertEquals("Frost", registry.forDepth(3).themeName());
        assertEquals("Winterfell", registry.forDepth(4).themeName());
        assertEquals("Crypt", registry.forDepth(5).themeName());
    }

    @Test
    void everyLevelIsInternallyConsistent() {
        GameWorld world = new GameWorld((new Player("Tester")));

        for(DungeonLevel level : world.getLevels()) {
            Set<String> allowed = switch (level.getThemeName()) {
                case "Crypt" -> Set.of("Skeleton", "Crypt Rat", "Wight", "Ghoul", "Bone Priest", "Bone Tyrant");
                case "Forge" -> Set.of("Imp", "Slag Hound", "Ember Sprite", "Forge Golem", "Forge Tyrant");
                case "Frost" -> Set.of("Frost Wight", "Rime Stalker", "Ice Lurker", "Hoar Shade", "Rime Tyrant");
                case "Winterfell" -> Set.of("White Walkers", "Direwolf", "Night King Tyrant");
                default -> Set.of();
            };
            for(Room room : level.getRooms()) {
                for(Monster m : room.getMonsters()) {
                    assertTrue(allowed.contains(m.getName()),
                            m.getName() + "does not belong on a " + level.getThemeName() + " level");
                }
            }
        }
    }

    // Test US-2.3 Factory Method

    @Test
    void populateIsFinalSoSubclassesCannotBreakTheOrder() throws Exception{
        Method populate = RoomPopulator.class.getDeclaredMethod("populate", Room.class, int.class);
        assertTrue(Modifier.isFinal(populate.getModifiers()),
                "populate() must be final -- the ORDER is the invariant this class exists to protect.");
    }

    @Test
    void createEncounterIsAbstractSoEverySubclassMustAnswerIt() throws Exception{
        Method create = RoomPopulator.class.getDeclaredMethod("createEncounter", int.class);
        assertTrue(Modifier.isAbstract(create.getModifiers()),
                "create() must be abstract -- this is the Factory Method SubClasses MUST implement.");
    }

    @Test
    void aStandardRoomGetsNoChest() {
        RoomPopulator standard = new StandardRoomPopulator(new CryptThemeKit(factory));
        Room room = new Room("Test");
        standard.populate(room, 1);

        assertFalse(room.hasChest());
        assertFalse(room.getFlavor().isBlank(), "populate() must always set flavor text");
    }

    @Test
    void aTreasureRoomGetsAChestWithThemeAppropriateLoot() {
        RoomPopulator treasure = new TreasureRoomPopulator(new ForgeThemeKit(factory));
        Room room = new Room("Test");
        treasure.populate(room, 2);

        assertTrue(room.hasChest());
        assertTrue(room.getChest().getContents().size() >= 2);
        assertEquals(1, room.getMonsters().size(), "a treasure room has exactly one guard.");
    }

    @Test
    void theFinalRoomOfTheDunegonHoldsTheBoss() {
        GameWorld world = new GameWorld((new Player("Tester")));
        List<DungeonLevel> levels = world.getLevels();
        DungeonLevel last = levels.get(levels.size() - 1);
        Room finalRoom = last.getRooms().get(last.getRooms().size() - 1);

        assertTrue(finalRoom.getMonsters().stream().anyMatch(m -> m.getName().contains("Tyrant")),
                "the last room of the last level should contain a boss");
    }

    @Test
    void theWorldStillGeneratesDeterministicallyFromASeed() {
        RandomSource.getInstance().reseed(77L);
        int first = new GameWorld(new Player("A")).totalMonsters();
        RandomSource.getInstance().reseed(77L);
        int second = new GameWorld(new Player("B")).totalMonsters();

        assertEquals(first, second, "Singleton's MUST still guarantee a deterministic world.");
    }
}
