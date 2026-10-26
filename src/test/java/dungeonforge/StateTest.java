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
import dungeonforge.states.DeadState;
import dungeonforge.states.EnragedState;
import dungeonforge.states.FleeingState;
import dungeonforge.states.HuntingState;
import dungeonforge.states.IdleState;
import dungeonforge.states.MonsterState;
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
}
