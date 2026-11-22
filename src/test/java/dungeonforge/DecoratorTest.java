package dungeonforge;

import dungeonforge.commands.Command;
import dungeonforge.commands.CommandHistory;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;
import dungeonforge.commands.NoCommand;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.EventBus;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.BasicWeapon;
import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.FlamingWeapon;
import dungeonforge.items.FrostWeapon;
import dungeonforge.items.KeenWeapon;
import dungeonforge.items.VampiricWeapon;
import dungeonforge.items.Weapon;
import dungeonforge.states.HuntingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 9 -- Sprint 7. The Decorator pattern. */
class DecoratorTest {

    private GameContext ctx;
    private CommandParser parser;
    private CommandHistory history;
    private Player player;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
        player = new Player("Tester");
        GameWorld world = new GameWorld(player);
        EventBus bus = new EventBus();
        history = new CommandHistory();
        ctx = new GameContext(world, player, bus, new Combat(bus), history);
        parser = new CommandParser(new QuestTracker(bus), new AchievementSystem(bus));
    }

    private void run(String input) {
        Command c = parser.parse(input, ctx);
        // A verb that is illegal in the current mode parses to NoCommand and does nothing, and
        // the failure then surfaces several assertions later looking like a pattern bug.
        assertFalse(c instanceof NoCommand,
                "SETUP ERROR, not a pattern bug: '" + input + "' was refused before it ran. "
              + "Mode is " + ctx.getState().name() + ", whose verbs are " + ctx.allowedVerbs()
              + ". Did you call ctx.syncState() after putting a monster in the room?");
        c.execute();
        history.push(c);
    }

    /** Puts a hostile in the room THE WAY WALKING IN WOULD -- in the list, and mode synced. */
    private Monster spawnHostile(String name, int maxHp, int attack, int xpReward) {
        Monster m = new Monster(name, maxHp, attack, xpReward);
        m.setStrategy(new dungeonforge.behavior.AggressiveStrategy());
        ctx.getCurrentRoom().getMonsters().add(m);
        ctx.syncState();
        return m;
    }

    private static Weapon sword() { return new BasicWeapon("Iron Sword", 3.0, 40, 8); }

    // ================= US-7.1: one decorator per enchantment =================

    @Test
    void aDecoratorIsAWeaponAndCanStandWhereverAWeaponStands() {
        Weapon w = new FlamingWeapon(sword());
        assertInstanceOf(Weapon.class, w, "a decorator must BE a Weapon, not merely hold one");
        assertEquals(3.0, w.getWeight(), 0.001, "unchanged properties must delegate through");
    }

    @Test
    void eachEnchantmentAddsItsOwnBonusAndNothingElse() {
        assertEquals(8,  sword().getDamage());
        assertEquals(12, new FlamingWeapon(sword()).getDamage());      // 8 + 4
        assertEquals(11, new FrostWeapon(sword()).getDamage());        // 8 + 3
        assertEquals(8,  new VampiricWeapon(sword()).getDamage());     // drains, does not hit harder
    }

    @Test
    void theNameReadsTheStackFromTheOutsideIn() {
        assertEquals("Vampiric Flaming Iron Sword",
                new VampiricWeapon(new FlamingWeapon(sword())).getName());
    }

    @Test
    void valueAccumulatesThroughTheStack() {
        assertEquals(40,           sword().getValue());
        assertEquals(40 + 60,      new FlamingWeapon(sword()).getValue());
        assertEquals(40 + 60 + 45, new FrostWeapon(new FlamingWeapon(sword())).getValue());
    }

    @Test
    void aDecoratorRefusesToWrapNothing() {
        assertThrows(NullPointerException.class, () -> new FlamingWeapon(null));
    }

    // ================= US-7.2: any enchantment, any depth, any order =================

    /**
     * THE HEADLINE. The starter's EnchantmentForge returned null for this, because there was no
     * class called FlamingFlamingWeapon and there was never going to be one.
     */
    @Test
    void theSameEnchantmentCanBeAppliedTwice() {
        Weapon w = new FlamingWeapon(new FlamingWeapon(sword()));
        assertEquals(16, w.getDamage(), "8 + 4 + 4");
        assertEquals("Flaming Flaming Iron Sword", w.getName());
    }

    @Test
    void thereIsNoCeilingOnDepth() {
        Weapon w = sword();
        for (int i = 0; i < 12; i++) w = new FrostWeapon(w);
        assertEquals(8 + 12 * 3, w.getDamage());
    }

    @Test
    void theRegistryNeverReturnsNullForAKnownEnchantment() {
        Weapon w = sword();
        for (String e : EnchantmentRegistry.known()) {
            for (int i = 0; i < 3; i++) {
                w = EnchantmentRegistry.apply(e, w);
                assertNotNull(w, "wrapping " + e + " must always succeed, at any depth");
            }
        }
    }

    @Test
    void anUnknownEnchantmentFailsLoudlyRatherThanReturningNull() {
        assertFalse(EnchantmentRegistry.isKnown("splendid"));
        assertThrows(IllegalArgumentException.class,
                () -> EnchantmentRegistry.apply("splendid", sword()));
    }

    /** US-7.2 -- the stale-hand bug. Decorator did not fix this; the command did. */
    @Test
    void enchantingTheWeaponInYourHandChangesWhatYouHitWith() {
        player.addItem(sword());
        run("equip sword");
        int armed = player.getAttackPower();

        run("enchant sword with flaming");
        assertEquals(armed + 4, player.getAttackPower(),
                "the equipped slot must follow the weapon through the enchantment");
        assertEquals("Flaming Iron Sword", player.getEquippedWeapon().getName());
    }

    @Test
    void undoingAnEnchantmentPutsTheOldWeaponBackInYourHand() {
        player.addItem(sword());
        run("equip sword");
        run("enchant sword with vampiric");
        assertEquals("Vampiric Iron Sword", player.getEquippedWeapon().getName());

        assertTrue(history.undoLast());
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());
        assertEquals(1, player.getInventory().size());
    }

    // ================= US-7.3: the fourth enchantment =================

    @Test
    void keenMultipliesWhereTheOthersAdd() {
        assertEquals(12, new KeenWeapon(sword()).getDamage(), "round(8 * 1.5)");
    }

    @Test
    void theFourthEnchantmentNeededNoChangeToTheOtherThree() {
        assertEquals(12, new FlamingWeapon(sword()).getDamage());
        assertEquals(11, new FrostWeapon(sword()).getDamage());
        assertEquals(4,  EnchantmentRegistry.known().size());
        assertTrue(EnchantmentRegistry.known().contains("keen"));
    }

    // ================= US-7.4: =================
    @Test
    void orderChangesTheResultForMultiplyingDecorators() {
        int keenThenFire = new FlamingWeapon(new KeenWeapon(sword())).getDamage();
        int fireThenKeen = new KeenWeapon(new FlamingWeapon(sword())).getDamage();

        assertEquals(16, keenThenFire);
        assertEquals(18, fireThenKeen);
        assertNotEquals(keenThenFire, fireThenKeen,
                "If these were equal, order would not matter nad this test would be pointless");
    }

    @Test
    void orderChangesHowMuchAVampiricWeaponDrinks() {
        assertEquals(3, new VampiricWeapon(new FlamingWeapon(sword())).lifesteal());
        assertEquals(2, new FlamingWeapon(new VampiricWeapon(sword())).lifesteal());
    }

    @Test
    void twoVampiricLayersDrinkTwice() {
        assertEquals(4, new VampiricWeapon(new VampiricWeapon(sword())).lifesteal(),
                "the inner drinks 2 from the base 8 ; the outer drinks 2 more and adds the inner's");
    }

    // ================= the payoff: Combat never learned any of this =================

    @Test
    void combatHealsTheWielderWithoutKnowingWhatAnEnchantmentIs() {
        Monster m = spawnHostile("Test Dummy", 200, 1, 5);
        // Reaching in to set the state is right HERE and wrong in CommandTest's undo test, and
        // the difference is what the test is about. This test measures lifesteal against a
        // counter-attack, so "a monster that will fight back" is a PRECONDITION. The undo test
        // spends the free first blow instead, because there the first-blow rule is itself one of
        // the rules being checked. Overwrite state you depend on; never state you assert on.
        m.setState(new HuntingState());

        player.addItem(new VampiricWeapon(new FlamingWeapon(sword())));
        run("equip sword");
        player.setHp(20);

        int before = player.getHp();
        run("attack");

        assertTrue(player.getHp() > before - 12,
                "lifesteal must offset some of the counter-attack; got " + player.getHp());
        assertTrue(m.getHp() < 200);
    }

    // ============= US-7.5 (carried over from Sprint 7): taking it back off =============

    @Test
    void peelingAPlainWeaponGivesBackTheSameObject() {
        Weapon plain = sword();
        assertSame(plain, plain.peel(),
                "a plain weapon peels to ITSELF -- that identity is why DisenchantCommand "
              + "needs no instanceof");
    }

    @Test
    void peelingADecoratorGivesBackWhatWasUnderneath() {
        Weapon bare = sword();
        Weapon frosted = new FrostWeapon(bare);
        assertSame(bare, frosted.peel(), "one layer off must be the exact object it wrapped");
    }

    @Test
    void disenchantRemovesOnlyTheOUTERMOSTLayer() {
        player.addItem(new FlamingWeapon(new FrostWeapon(sword())));
        assertEquals(15, ((Weapon) player.findItem("sword")).getDamage());   // 8 + 3 + 4

        run("disenchant sword");

        Weapon now = (Weapon) player.findItem("sword");
        assertEquals("Frost Iron Sword", now.getName(),
                "Flaming went on last, so Flaming comes off first");
        assertEquals(11, now.getDamage(), "8 + 3 -- the frost layer survived");
    }

    @Test
    void thereIsNoWayToReachPastTheOuterLayer() {
        player.addItem(new FlamingWeapon(new FrostWeapon(sword())));

        run("disenchant sword");
        assertEquals("Frost Iron Sword", ((Weapon) player.findItem("sword")).getName());
        run("disenchant sword");
        assertEquals("Iron Sword", ((Weapon) player.findItem("sword")).getName());

        // The point of this test is the name that NEVER appeared. "Flaming Iron Sword" was not
        // reachable by any number of disenchants, because removing the inner layer and keeping
        // the outer one is not a thing a decorator chain can do. Last on, first off.
        assertEquals(8, ((Weapon) player.findItem("sword")).getDamage());
    }

    @Test
    void disenchantingTheWeaponInYourHandChangesWhatYouHitWith() {
        player.addItem(new FlamingWeapon(sword()));
        run("equip sword");
        int armed = player.getAttackPower();

        run("disenchant sword");
        assertEquals(armed - 4, player.getAttackPower(),
                "the equipped slot must follow the weapon back down too -- the stale-hand bug "
              + "has a mirror image, and it is not fixed by the pattern either");
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());
    }

    @Test
    void disenchantingAPlainWeaponIsRefusedAndChangesNothing() {
        player.addItem(sword());
        run("equip sword");
        int armed = player.getAttackPower();

        run("disenchant sword");
        assertEquals(armed, player.getAttackPower());
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());
        assertEquals(1, player.getInventory().size(), "a refusal must not duplicate the weapon");
    }

    @Test
    void undoingADisenchantPutsTheEnchantmentBack() {
        player.addItem(new FlamingWeapon(sword()));
        run("equip sword");
        run("disenchant sword");
        assertEquals("Iron Sword", player.getEquippedWeapon().getName());

        assertTrue(history.undoLast());
        assertEquals("Flaming Iron Sword", player.getEquippedWeapon().getName(),
                "the snapshot already stores the hand -- DisenchantCommand needs no undo() of "
              + "its own, and writing one would double-restore");
        assertEquals(1, player.getInventory().size());
    }

    @Test
    void disenchantCommandContainsNoTypeTestForDecorators() throws Exception {
        String code = codeOnly(java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/dungeonforge/commands/DisenchantCommand.java")));

        assertFalse(code.contains("instanceof WeaponDecorator"),
                "peel() is on Weapon precisely so this command never has to ask what it is "
              + "holding. The type test you want to write is usually a missing method.");
        assertFalse(code.contains("unwrap"),
                "unwrap() is gone; peel() replaced it");
    }

    /**
     * Strips comments before searching, because a source-inspection assertion that reads the
     * commentary is tripped by any comment that MENTIONS the thing it is looking for --
     * including the comment explaining why the code does not do it.
     */
    private static String codeOnly(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", "")
                     .replaceAll("(?m)//.*$", "");
    }
}
