package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.events.EventType;
import dungeonforge.factory.MonsterDef;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.MonsterSource;
import dungeonforge.items.BasicWeapon;
import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.Weapon;
import dungeonforge.legacy.LegacyBestiary;
import dungeonforge.legacy.LegacyBestiaryAdapter;
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
}
