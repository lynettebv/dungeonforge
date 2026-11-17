package dungeonforge;

import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.CombatStrategy;
import dungeonforge.behavior.HealerStrategy;
import dungeonforge.behavior.SkittishStrategy;
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
import dungeonforge.core.Room;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.EventBus;
import dungeonforge.events.QuestTracker;
import dungeonforge.states.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 8 -- Sprint 6. The State pattern, at two levels. */
class StateTest {

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
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        history = new CommandHistory();
        ctx = new GameContext(world, player, bus, new Combat(bus), history);
        parser = new CommandParser(quests, achievements);
    }

    private void run(String input) {
        Command c = parser.parse(input, ctx);
        c.execute();
        history.push(c);
    }

    private Monster monster(String name, int hp, int atk) {
        Monster m = new Monster(name, hp, atk, 5);
        m.setStrategy(new AggressiveStrategy());
        return m;
    }

    // ---------- US-6.1: the game has modes ----------

    @Test
    void aQuietRoomMeansExploring() {
        assertInstanceOf(ExploringState.class, ctx.getState());
    }

    @Test
    void aLivingMonsterPutsTheGameIntoCombat() {
        ctx.getCurrentRoom().getMonsters().add(monster("Skeleton", 20, 3));
        ctx.syncState();
        assertInstanceOf(CombatState.class, ctx.getState());
    }

    @Test
    void clearingTheRoomReturnsToExploring() {
        Monster m = monster("Skeleton", 5, 1);
        ctx.getCurrentRoom().getMonsters().add(m);
        ctx.syncState();
        assertInstanceOf(CombatState.class, ctx.getState());

        run("attack");
        assertFalse(m.isAlive());
        ctx.syncState();
        assertInstanceOf(ExploringState.class, ctx.getState());
    }

    /** ILLEGAL, not merely rejected: combat does not list movement at all. */
    @Test
    void movementIsNotAvailableDuringCombat() {
        ctx.getCurrentRoom().getMonsters().add(monster("Skeleton", 30, 2));
        ctx.syncState();

        assertFalse(ctx.allowedVerbs().contains("north"));
        assertInstanceOf(NoCommand.class, parser.parse("north", ctx));
    }

    @Test
    void attackIsNotAvailableWhileExploring() {
        assertFalse(ctx.allowedVerbs().contains("attack"));
        assertInstanceOf(NoCommand.class, parser.parse("attack", ctx));
    }

    /** The rule lives in ONE place now, not in a guard inside each command. */
    @Test
    void commandsNoLongerCarryTheirOwnModeGuards() throws Exception {
        for (String file : new String[]{"MoveCommand", "TakeCommand", "DropCommand"}) {
            String source = java.nio.file.Files.readString(
                    java.nio.file.Path.of("src/main/java/dungeonforge/commands/" + file + ".java"));
            assertFalse(source.contains("hasLivingMonsters"),
                    file + " should not check the game mode itself");
        }
    }

    @Test
    void helpShowsOnlyWhatIsLegalRightNow() {
        int exploringVerbs = ctx.allowedVerbs().size();
        ctx.getCurrentRoom().getMonsters().add(monster("Skeleton", 30, 2));
        ctx.syncState();

        assertNotEquals(exploringVerbs, ctx.allowedVerbs().size(),
                "the legal verb list must change with the mode");
    }

    // ---------- US-6.2: dedicated screens ----------

    @Test
    void thePackIsItsOwnModeYouEnterAndLeaveOnPurpose() {
        run("pack");
        assertInstanceOf(InventoryState.class, ctx.getState());
        run("back");
        assertInstanceOf(ExploringState.class, ctx.getState());
    }

    /** A mode the player chose must not be yanked away by the world changing. */
    @Test
    void openingThePackSurvivesAMonsterArriving() {
        run("pack");
        ctx.getCurrentRoom().getMonsters().add(monster("Skeleton", 20, 3));
        ctx.syncState();

        assertInstanceOf(InventoryState.class, ctx.getState(),
                "syncState must leave a deliberately-entered sub-state alone");
    }

    @Test
    void deathIsAStateNotABoolean() {
        player.setHp(0);
        ctx.syncState();

        assertInstanceOf(GameOverState.class, ctx.getState());
        assertEquals(4, ctx.allowedVerbs().size());
        assertFalse(ctx.allowedVerbs().contains("north"));
    }

    // ---------- US-6.3: monsters have a lifecycle ----------

    @Test
    void aMonsterStartsIdleAndCannotAct() {
        Monster m = monster("Skeleton", 20, 3);
        assertInstanceOf(IdleState.class, m.getState());
        assertFalse(m.getState().canAct());
    }

    @Test
    void idleBecomesHunting() {
        Monster m = monster("Skeleton", 20, 3);
        assertInstanceOf(HuntingState.class, m.getState().next(m, player));
    }

    @Test
    void aWoundedHunterEnrages() {
        Monster m = monster("Skeleton", 20, 3);
        m.setState(new HuntingState());
        m.takeDamage(12);                 // 40% left, under the 45% enrage threshold

        assertInstanceOf(EnragedState.class, m.getState().next(m, player));
    }

    @Test
    void aBadlyWoundedMonsterFlees() {
        Monster m = monster("Skeleton", 20, 3);
        m.setState(new HuntingState());
        m.takeDamage(16);                 // 20% left, under the 30% flee threshold

        assertInstanceOf(FleeingState.class, m.getState().next(m, player));
    }

    @Test
    void deadIsTerminalAndCannotAct() {
        Monster m = monster("Skeleton", 20, 3);
        m.setState(new HuntingState());
        m.takeDamage(20);

        MonsterState next = m.getState().next(m, player);
        assertInstanceOf(DeadState.class, next);
        assertFalse(next.canAct());
        assertInstanceOf(DeadState.class, next.next(m, player), "the dead stay dead");
    }

    @Test
    void enrageAndFleeingAreTheOnlyStatesThatChangeDamage() {
        assertEquals(1.0, new HuntingState().damageMultiplier(), 0.001);
        assertEquals(0.5, new FleeingState().damageMultiplier(), 0.001);
        assertEquals(EnragedState.DAMAGE_BONUS, new EnragedState().damageMultiplier(), 0.001);
    }

    /** Combat must not enumerate state names any more. */
    @Test
    void combatNoLongerBranchesOnStateNames() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/dungeonforge/core/Combat.java"));

        assertFalse(source.contains("getStateName"));
        assertFalse(source.contains("\"enraged\""));
        assertFalse(source.contains("\"fleeing\""));
        assertFalse(source.contains("\"idle\""));
    }

    // ---------- US-6.4: State and Strategy are different things ----------

    @Test
    void aMonsterHasBothAStateAndAStrategy() {
        Monster m = monster("Bone Priest", 20, 3);
        m.setStrategy(new HealerStrategy());

        assertNotNull(m.getState());
        assertNotNull(m.getStrategy());
        assertEquals("healer", m.getStrategy().name());
        assertEquals("idle", m.getState().label());
    }

    /** THE demonstration: a STATE transition can change the STRATEGY. */
    @Test
    void enteringRageMakesEvenAHealerAggressive() {
        Monster priest = monster("Bone Priest", 20, 3);
        priest.setStrategy(new HealerStrategy());
        assertEquals("healer", priest.getStrategy().name());

        priest.setState(new EnragedState());

        assertEquals("aggressive", priest.getStrategy().name(),
                "EnragedState.onEnter should swap the strategy -- state decides WHEN, strategy is WHAT");
    }

    @Test
    void fleeingSwapsTheStrategyToSkittish() {
        Monster m = monster("Slag Hound", 20, 3);
        m.setState(new FleeingState());
        assertInstanceOf(SkittishStrategy.class, m.getStrategy());
    }

    /** ...but the arrow only points one way. A strategy never sets a state. */
    @Test
    void changingStrategyDoesNotChangeState() {
        Monster m = monster("Skeleton", 20, 3);
        m.setState(new HuntingState());

        CombatStrategy before = m.getStrategy();
        m.setStrategy(new SkittishStrategy());

        assertInstanceOf(HuntingState.class, m.getState(),
                "swapping tactics must not move the monster through its lifecycle");
        assertNotSame(before, m.getStrategy());
    }

    // ---------- regression ----------

    @Test
    void earlierWeeksStillHold() {
        assertEquals(19, ctx.getWorld().getMonsterFactory().blueprintCount(), "Week 4");

        Room start = ctx.getCurrentRoom();
        run("north");
        assertNotSame(start, ctx.getCurrentRoom());
        assertTrue(history.undoLast(), "Week 7 undo");
        assertSame(start, ctx.getCurrentRoom());

        RandomSource.getInstance().reseed(7L);
        int a = new GameWorld(new Player("A")).totalMonsters();
        RandomSource.getInstance().reseed(7L);
        int b = new GameWorld(new Player("B")).totalMonsters();
        assertEquals(a, b, "Week 3 determinism");
    }

    // ---------- US-6.7 (carried over from Sprint 6): one answer to "is the game over?" ----------

    /**
     * THE POINT OF US-6.7. Two facts, two representations, neither consulting the other:
     *
     *   the ADVENTURE is over  ->  a terminal GameState
     *   the SESSION  is over   ->  GameContext.running
     *
     * Dying sets only the first, which is what makes GameOverState's verb list reachable at
     * last. Before this story the loop exited the instant the player died, so nobody ever got
     * a turn in Game Over mode and that verb list was dead code.
     */
    @Test
    void deathIsATerminalStateButTheSessionKeepsRunning() {
        Monster butcher = new Monster("Butcher", 500, 999, 1);
        butcher.setStrategy(new AggressiveStrategy());
        ctx.getCurrentRoom().getMonsters().add(butcher);
        ctx.syncState();
        butcher.setState(new HuntingState());
        player.setHp(1);

        run("attack");
        ctx.syncState();

        assertFalse(player.isAlive());
        assertInstanceOf(GameOverState.class, ctx.getState(), "death must land in Game Over");
        assertTrue(ctx.getState().isTerminal(), "the adventure is over");
        assertTrue(ctx.isRunning(), "but the SESSION is not -- you can still read your record");
    }

    @Test
    void theGameOverVerbListIsReachableAtLast() {
        player.setHp(0);
        ctx.syncState();

        assertInstanceOf(GameOverState.class, ctx.getState());
        assertTrue(ctx.allowedVerbs().contains("quests"), "you may read your record");
        assertTrue(ctx.allowedVerbs().contains("quit"));
        assertFalse(ctx.allowedVerbs().contains("north"), "but you may not walk");
        assertFalse(ctx.allowedVerbs().contains("attack"));
    }

    /** Terminal means terminal. A sync must not put a finished game back on its feet. */
    @Test
    void gameOverIsSomewhereYouStay() {
        player.setHp(0);
        ctx.syncState();
        assertInstanceOf(GameOverState.class, ctx.getState());

        player.heal(999);                 // even if something revives the hit points
        ctx.syncState();
        ctx.syncState();

        assertInstanceOf(GameOverState.class, ctx.getState(),
                "the terminal guard must come BEFORE the Exploring/Combat branches");
    }

    @Test
    void quitEndsTheAdventureAndTheSessionTogether() {
        assertTrue(ctx.isRunning());
        run("quit");

        assertInstanceOf(GameOverState.class, ctx.getState(), "the adventure ended");
        assertFalse(ctx.isRunning(), "and so did the session");
    }

    @Test
    void aSyncDoesNotRestartASessionThePlayerEnded() {
        run("quit");
        ctx.syncState();
        ctx.syncState();
        assertFalse(ctx.isRunning());
    }

    /**
     * US-6.7's absence assertion, and the one that would have caught the original defect.
     *
     * syncState() decides the mode from the WORLD. The moment it reads a session flag, the
     * game-over rule exists in two places, and Main makes it three.
     */
    @Test
    void syncStateDecidesTheModeFromTheWorldAndNeverFromASessionFlag() throws Exception {
        String source = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Path.of("src/main/java/dungeonforge/commands/GameContext.java")));
        int syncAt = source.indexOf("public void syncState()");
        assertTrue(syncAt > 0, "syncState() should exist");
        String body = codeOnly(source.substring(syncAt, source.indexOf("public List<String> allowedVerbs()", syncAt)));

        assertFalse(body.contains("running"),
                "syncState() must not consult the session flag -- that is what put one rule "
              + "in three places. Death is a world fact; quitting is a session fact.");
    }

    @Test
    void theGameLoopAsksOneQuestionNotTwo() throws Exception {
        String main = codeOnly(new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Path.of("src/main/java/dungeonforge/Main.java"))));
        assertFalse(main.contains("isRunning() && ctx.getPlayer().isAlive()"),
                "the loop condition must not re-implement the game-over rule");
        assertFalse(main.contains("!ctx.isRunning() || !ctx.getPlayer().isAlive()"),
                "nor must the script runner");
    }

    /**
     * Source with every comment stripped out, for the absence assertions above.
     *
     * This exists because a naive source.contains("running") is tripped by a COMMENT that
     * merely mentions the word -- including a comment explaining that the code deliberately
     * does not read it. An assertion that prose can break is an assertion that will be "fixed"
     * by deleting the prose, which is backwards.
     *
     * If you write your own absence assertions, strip comments first. Grep the code, not the
     * commentary.
     */
    private static String codeOnly(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", "")   // block comments
                     .replaceAll("(?m)//.*$", "");            // line comments
    }
}
