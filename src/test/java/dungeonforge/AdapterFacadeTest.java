package dungeonforge;

import dungeonforge.commands.GameContext;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.factory.MonsterDef;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.MonsterSource;
import dungeonforge.items.BasicWeapon;
import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.Weapon;
import dungeonforge.legacy.LegacyBestiary;
import dungeonforge.legacy.LegacyBestiaryAdapter;
import dungeonforge.session.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 12 -- Adapter (US-9.1), Facade (US-9.2), the replay receipt (US-9.3), carry-over (US-8.5). */
class AdapterFacadeTest {

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
    }

    private static LegacyBestiaryAdapter adapter() {
        return new LegacyBestiaryAdapter(new LegacyBestiary("bestiary.csv"), "test");
    }

    private static MonsterDef byId(List<MonsterDef> defs, String id) {
        return defs.stream().filter(d -> d.id.equals(id)).findFirst()
                .orElseThrow(() -> new AssertionError("no blueprint with id " + id));
    }

    // ===================== US-9.1 : the ADAPTER =====================

    @Test
    void everyFieldOfOneRecordIsTranslated() {
        MonsterDef d = byId(adapter().definitions(), "barrow_wight");
        assertEquals("barrow_wight", d.id,       "UPPER_SNAKE id must come across lower case");
        assertEquals("Barrow Wight", d.name);
        assertEquals(18, d.hp,                   "4 hit dice -> 4*4+2");
        assertEquals(5,  d.attack,               "1d6+2 -> floor(7/2) + 2");
        assertEquals("crypt", d.theme,           "ZONE 1 -> crypt");
        assertFalse(d.boss,                      "TIER MINION -> not a boss");
        assertEquals("aggressive", d.strategy,   "AI BRAWLER -> aggressive");
    }

    @Test
    void diceExpressionsBecomeFlatAttackValues() {
        List<MonsterDef> defs = adapter().definitions();
        assertEquals(9, byId(defs, "crypt_warden").attack,  "2d6+3 -> 2*3 + 3");
        assertEquals(2, byId(defs, "grave_moth").attack,    "1d4 with no bonus -> floor(5/2)");
        assertEquals(5, byId(defs, "hoarfrost_elk").attack, "2d4+1 -> 2*2 + 1");
    }

    @Test
    void hitDiceBecomeHitPoints() {
        List<MonsterDef> defs = adapter().definitions();
        assertEquals(46, byId(defs, "crypt_warden").hp, "11 hit dice");
        assertEquals(10, byId(defs, "grave_moth").hp,   "2 hit dice");
    }

    @Test
    void zoneNumbersBecomeThemeNames() {
        Set<String> themes = new HashSet<>();
        for (MonsterDef d : adapter().definitions()) themes.add(d.theme);
        assertEquals(Set.of("crypt", "forge", "frost", "winterfell"), themes,
                "all four zone codes must map, and nothing else may appear");
    }

    @Test
    void tierBecomesTheBossFlag() {
        List<MonsterDef> defs = adapter().definitions();
        assertTrue(byId(defs, "crypt_warden").boss, "TIER BOSS");
        long bosses = defs.stream().filter(d -> d.boss).count();
        assertEquals(1, bosses, "exactly one record in bestiary.csv is a BOSS");
    }

    @Test
    void legacyAiNamesMapOntoOurStrategies() {
        List<MonsterDef> defs = adapter().definitions();
        assertEquals("aggressive", byId(defs, "bellows_thrall").strategy,  "BRAWLER");
        assertEquals("ranged",     byId(defs, "cinder_wasp").strategy,     "ARCHER");
        assertEquals("skittish",   byId(defs, "grave_moth").strategy,      "COWARD");
        assertEquals("healer",     byId(defs, "slag_chirurgeon").strategy, "MEDIC");
    }

    @Test
    void anAiWithNoCounterpartIsRecordedRatherThanSwallowed() {
        LegacyBestiaryAdapter a = adapter();
        List<MonsterDef> defs = a.definitions();

        assertEquals("aggressive", byId(defs, "tunnel_sapper").strategy,
                "SAPPER has no counterpart, so it must fall back to something playable");

        List<String> unmapped = a.translationNotes().stream()
                .filter(s -> s.contains("no counterpart")).toList();
        assertEquals(1, unmapped.size(),
                "the lossy conversion must be VISIBLE; an adapter that silently invents "
                        + "behaviour is indistinguishable from one that read the file correctly");
        assertTrue(unmapped.get(0).contains("SAPPER"), unmapped.get(0));
    }

    @Test
    void everyDerivedXpIsRecordedBecauseTheFileHasNoXpColumn() {
        LegacyBestiaryAdapter a = adapter();
        int records = a.definitions().size();
        long derived = a.translationNotes().stream().filter(s -> s.contains("xp derived")).count();
        assertEquals(records, derived,
                "bestiary.csv has no xp column, so EVERY xp value is derived and every one "
                        + "of them has to say so");
    }

    @Test
    void theAdapterCanBeAskedTwiceBecauseItRewindsTheCursorItself() {
        LegacyBestiaryAdapter a = adapter();
        List<MonsterDef> first = a.definitions();
        List<MonsterDef> second = a.definitions();
        assertEquals(10, first.size());
        assertEquals(first.size(), second.size(),
                "a cursor is single-use; hiding that is part of the adapter's job");
    }

    @Test
    void registeringTheLegacySourceAddsBlueprintsAndDisplacesNone() {
        MonsterFactory factory = new MonsterFactory();
        int before = factory.blueprintCount();
        assertEquals("bone_tyrant", factory.bossIdForTheme("crypt"));

        for (MonsterDef d : adapter().definitions()) factory.register(d);

        assertEquals(before + 10, factory.blueprintCount(), "ten new blueprints");
        assertEquals("bone_tyrant", factory.bossIdForTheme("crypt"),
                "the legacy crypt boss must not displace the one the game already had");
        assertTrue(factory.has("tunnel_sapper"));
        assertTrue(factory.idsForTheme("winterfell").contains("weeping_sentinel"));
    }

    @Test
    void theFactoryNeverLearnsThatACsvExists() throws Exception {
        String code = codeOnly(java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/dungeonforge/factory/MonsterFactory.java")));
        for (String leak : List.of("bestiary", "csv", "LegacyBestiary", "MOB_ID", "HIT_DICE", "ZONE")) {
            assertFalse(code.contains(leak),
                    "MonsterFactory must not know the legacy format exists; found: " + leak);
        }
    }

    @Test
    void anotherSourceNeedsNoChangeAnywhereElse() {
        // A second source, in a format nobody has written a reader for, arriving through the
        // same door. This is the whole claim of US-9.1, as a test rather than a sentence.
        MonsterSource invented = new MonsterSource() {
            @Override public String sourceName() { return "invented"; }
            @Override public List<MonsterDef> definitions() {
                List<MonsterDef> out = new ArrayList<>();
                out.add(new MonsterDef("paper_golem", "Paper Golem", 12, 3, 8, "forge"));
                return out;
            }
        };
        MonsterFactory factory = new MonsterFactory();
        for (MonsterDef d : invented.definitions()) factory.register(d);
        assertTrue(factory.has("paper_golem"));
        assertTrue(factory.idsForTheme("forge").contains("paper_golem"));
    }

    // ===================== US-9.2 : the FACADE =====================

    @Test
    void theSessionSubscribesTheListenerThatNobodyEverWiredUp() {
        GameSession session = GameSession.start("Tester");
        assertTrue(session.listenerCount() >= 5,
                "quests, achievements, log, DAMAGE METER and the transcript; got "
                        + session.listenerCount());
    }

    @Test
    void theDamageMeterActuallyFiresNow() {
        GameSession session = GameSession.start("Tester");
        int threshold = (int) Math.round(0.25 * GameConfig.getInstance().getDouble("playerStartingHp"));

        session.getPlayer().setHp(threshold - 1);
        session.getBus().publish(GameEvent.of(EventType.DAMAGE_TAKEN, "amount", 1));

        boolean warned = session.transcript().stream().anyMatch(s -> s.contains("DANGER"));
        assertTrue(warned,
                "DamageMeter has existed since Week 5 and had never fired, because main() "
                        + "never subscribed it. The facade owns the listener list now.");
    }

    @Test
    void legacyMonstersAppearInAGeneratedDungeon() {
        // BOTH assembly paths, because they are two separate pieces of ordering code and an
        // earlier version of this test only checked one of them. A mutation that broke the
        // order in start() passed, which made the test decorative for the path players use.
        RandomSource.resetForTests();
        assertTrue(hasLegacyMonster(GameSession.start("Tester").getWorld()),
                "GameSession.start() must register every source BEFORE generating the world; "
                        + "blueprints registered afterwards can never spawn");

        RandomSource.resetForTests();
        assertTrue(hasLegacyMonster(GameSession.buildWorldOnly("Tester")),
                "and so must the diagnostic path, or --report describes a different dungeon");
    }

    private static boolean hasLegacyMonster(GameWorld world) {
        Set<String> legacyNames = new HashSet<>();
        for (MonsterDef d : adapter().definitions()) legacyNames.add(d.name);
        for (DungeonLevel level : world.getLevels()) {
            for (Room room : level.getRooms()) {
                for (Monster m : room.getMonsters()) {
                    if (legacyNames.contains(m.getName())) return true;
                }
            }
        }
        return false;
    }

    @Test
    void theTurnSequenceExistsExactlyOnce() throws Exception {
        String main = codeOnly(java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/dungeonforge/Main.java")));
        assertFalse(main.contains("syncState"),
                "Main must not run a turn itself; that is what let the script path drift");
        assertFalse(main.contains(".execute()"),
                "nor parse-and-execute; one copy of the turn sequence, in GameSession.runLine");

        String session = codeOnly(java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/dungeonforge/session/GameSession.java")));
        assertEquals(1, countOf(session, "ctx.syncState()"),
                "GameSession must call syncState in exactly one place");
    }

    @Test
    void aTurnDrivenThroughTheFacadeCannotWalkOutOfARoomWithALiveHostile() {
        GameSession session = GameSession.start("Tester");
        GameContext ctx = session.getContext();

        // Walk until we are standing in a room that still has something alive in it.
        for (int i = 0; i < 12 && !ctx.getCurrentRoom().hasLivingMonsters(); i++) {
            session.runLine("north");
        }
        assertTrue(ctx.getCurrentRoom().hasLivingMonsters(),
                "test needs a hostile room; none found within twelve moves");

        Room before = ctx.getCurrentRoom();
        assertEquals("Combat", ctx.getState().name(),
                "arriving beside a live hostile must put the game in combat mode -- the "
                        + "scripted path used to skip this and the mode never changed");

        session.runLine("north");
        assertSame(before, ctx.getCurrentRoom(),
                "combat mode has no movement verbs, so this must have been refused. Before "
                        + "the facade, a script walked straight past every monster in the dungeon.");
    }

    // ===================== US-9.3 : the replay receipt =====================

    @Test
    void theSameInputThroughTwoSessionsProducesIdenticalTranscripts() {
        List<String> input = List.of("look", "north", "status", "attack", "attack", "look", "status");

        RandomSource.resetForTests();
        GameSession a = GameSession.start("Delver");
        for (String line : input) a.runLine(line);

        RandomSource.resetForTests();
        GameSession b = GameSession.start("Delver");
        for (String line : input) b.runLine(line);

        assertEquals(a.transcript(), b.transcript(),
                "Week 7 promised that a recorded session reproduces exactly. Until the turn "
                        + "sequence existed in one place, that depended on which loop replayed it.");
        assertFalse(a.transcript().isEmpty(), "a transcript of nothing proves nothing");
    }

    @Test
    void theDiagnosticWorldAndThePlayWorldAgree() {
        RandomSource.resetForTests();
        int reportMonsters = GameSession.buildWorldOnly("Delver").totalMonsters();
        RandomSource.resetForTests();
        int playMonsters = GameSession.start("Delver").getWorld().totalMonsters();
        assertEquals(reportMonsters, playMonsters,
                "--report must describe the dungeon you actually play in");
    }

    // ===================== US-8.5 : the carry-over =====================

    @Test
    void enchantmentNamesAreCaseAndWhitespaceInsensitive() {
        assertTrue(EnchantmentRegistry.isKnown("FLAMING"), "a player typing in caps is not an error");
        assertTrue(EnchantmentRegistry.isKnown("  frost  "));
        Weapon w = EnchantmentRegistry.apply("  FLAMING ", new BasicWeapon("Iron Sword", 3.0, 40, 8));
        assertEquals("Flaming Iron Sword", w.getName());
        assertFalse(EnchantmentRegistry.isKnown("sparkly"), "unknown is still unknown");
    }

    @Test
    void theMonsterStateEventIsSpelledCorrectly() {
        assertNotNull(EventType.valueOf("MONSTER_STATE_CHANGED"));
        for (EventType t : EventType.values()) {
            assertFalse(t.name().contains("SATE"), "left-over typo: " + t.name());
        }
    }

    // ===================== helpers =====================

    /** Strips comments, so prose about a thing can never make an absence assertion pass or fail. */
    private static String codeOnly(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)//.*$", "");
    }
    private static int countOf(String text, String target) {
        return text.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }
}
