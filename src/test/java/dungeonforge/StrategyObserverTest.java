package dungeonforge;
import dungeonforge.behavior.Action;
import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.CombatStrategy;
import dungeonforge.behavior.HealerStrategy;
import dungeonforge.behavior.RangedStrategy;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.events.GameEventListener;
import dungeonforge.events.QuestTracker;
import dungeonforge.factory.MonsterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** WEEK 5 -- Sprint 3. Strategy and Observer. */
class StrategyObserverTest {
    @BeforeEach
    void reset() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
    }
    private Monster wounded(int maxHp, int damage) {
        Monster m = new Monster("Test", maxHp, 4, 5);
        m.takeDamage(damage);
        return m;
    }
    // ---------- US-3.1: Strategy ----------
    @Test
    void aMonsterHoldsItsBehaviourRatherThanBeingASubclassOfIt() {
        Monster m = new Monster("Skeleton", 10, 3, 5);
        m.setStrategy(new AggressiveStrategy());
        assertEquals("aggressive", m.getStrategy().name());
        assertEquals(Monster.class, m.getClass(),
                "behaviour must not be expressed by subclassing Monster");
    }
    @Test
    void aggressiveAlwaysAttacks() {
        Monster m = wounded(20, 0);
        Action a = new AggressiveStrategy().chooseAction(m, new Player("P"), new
                Room("r"));
        assertEquals(Action.Type.ATTACK, a.getType());
    }
    @Test
    void skittishFleesOnceBadlyWounded() {
        Monster m = wounded(20, 18); // 10% health, under the 30% flee threshold
        Action a = new SkittishStrategy().chooseAction(m, new Player("P"), new
                Room("r"));
        assertEquals(Action.Type.FLEE, a.getType());
    }
    @Test
    void skittishStillFightsWhenHealthy() {
        Monster m = wounded(20, 2); // 90% health
        Action a = new SkittishStrategy().chooseAction(m, new Player("P"), new
                Room("r"));
        assertEquals(Action.Type.ATTACK, a.getType());
    }
    @Test
    void healerMendsTheMostWoundedAlly() {
        Room room = new Room("r");
        Monster healer = new Monster("Bone Priest", 20, 3, 5);
        Monster hurt = wounded(20, 15);
        room.addMonster(healer);
        room.addMonster(hurt);
        Action a = new HealerStrategy().chooseAction(healer, new Player("P"),
                room);
        assertEquals(Action.Type.HEAL_ALLY, a.getType());
        assertSame(hurt, a.getTarget());
    }
    @Test
    void healerAttacksWhenNobodyNeedsMending() {
        Room room = new Room("r");
        Monster healer = new Monster("Bone Priest", 20, 3, 5);
        room.addMonster(healer);
        assertEquals(Action.Type.ATTACK,
                new HealerStrategy().chooseAction(healer, new Player("P"),
                        room).getType());
    }
    /** The data file decides behaviour, so a designer can retune it without a
     programmer. */
    @Test
    void theFactoryAssignsBehaviourFromTheDataFile() {
        MonsterFactory factory = new MonsterFactory();
        assertEquals("healer", factory.create("bone_priest",
                1).getStrategy().name());
        assertEquals("ranged", factory.create("imp", 1).getStrategy().name());
        assertEquals("skittish", factory.create("crypt_rat",
                1).getStrategy().name());
        assertEquals("aggressive", factory.create("skeleton",
                1).getStrategy().name());
    }
// ---------- US-3.2: the runtime swap ----------
    /** THE moment the pattern justifies itself. */
    @Test
    void theSameObjectBehavesDifferentlyAfterASwap() {
        Monster m = wounded(20, 18);
        Player p = new Player("P");
        Room r = new Room("r");
        m.setStrategy(new AggressiveStrategy());
        assertEquals(Action.Type.ATTACK, m.getStrategy().chooseAction(m, p,
                r).getType());
        m.setStrategy(new SkittishStrategy()); // one line, same object
        assertEquals(Action.Type.FLEE, m.getStrategy().chooseAction(m, p,
                r).getType());
    }
    @Test
    void combatSwapsAWoundedMonsterToSkittishAndAnnouncesIt() {
        EventBus bus = new EventBus();
        List<GameEvent> seen = new ArrayList<>();
        bus.subscribe(seen::add);
        Room room = new Room("r");
        Monster m = new Monster("Slag Hound", 25, 3, 5);
        m.setStrategy(new AggressiveStrategy());
        room.addMonster(m);
        new Combat(bus).fight(new Player("P"), room, 1);
        assertTrue(seen.stream().anyMatch(e -> e.getType() ==
                        EventType.STRATEGY_CHANGED),
                "a monster driven below the flee threshold should change tactics");
    }
    // ---------- US-3.3 and US-3.4: Observer ----------
    @Test
    void aSubscriberReceivesWhatIsPublished() {
        EventBus bus = new EventBus();
        List<GameEvent> seen = new ArrayList<>();
        bus.subscribe(seen::add);
        bus.publish(GameEvent.of(EventType.MONSTER_DIED, "name", "Skeleton", "xp",
                6));
        assertEquals(1, seen.size());
        assertEquals("Skeleton", seen.get(0).getString("name"));
        assertEquals(6, seen.get(0).getInt("xp"));
    }
    @Test
    void everySubscriberSeesEveryEvent() {
        EventBus bus = new EventBus();
        List<GameEvent> a = new ArrayList<>();
        List<GameEvent> b = new ArrayList<>();
        bus.subscribe(a::add);
        bus.subscribe(b::add);
        bus.publish(GameEvent.of(EventType.ROOM_CLEARED, "room", "L1R1"));
        assertEquals(1, a.size());
        assertEquals(1, b.size());
    }
    @Test
    void unsubscribingStopsDelivery() {
        EventBus bus = new EventBus();
        List<GameEvent> seen = new ArrayList<>();
        GameEventListener l = seen::add;
        bus.subscribe(l);
        bus.publish(GameEvent.message("one"));
        bus.unsubscribe(l);
        bus.publish(GameEvent.message("two"));
        assertEquals(1, seen.size());
    }
    /** A listener that removes itself mid-notification must not blow up the bus.
     */
    @Test
    void aListenerMayUnsubscribeItselfWhileBeingNotified() {
        EventBus bus = new EventBus();
        GameEventListener[] holder = new GameEventListener[1];
        holder[0] = e -> bus.unsubscribe(holder[0]);
        bus.subscribe(holder[0]);
        assertDoesNotThrow(() -> bus.publish(GameEvent.message("boom")));
        assertEquals(0, bus.listenerCount());
    }
    @Test
    void theQuestTrackerCountsWithoutCombatKnowingItExists() {
        EventBus bus = new EventBus();
        QuestTracker tracker = new QuestTracker(bus);
        bus.subscribe(tracker);
        for (int i = 0; i < 5; i++) {
            bus.publish(GameEvent.of(EventType.MONSTER_DIED, "name", "Skeleton",
                    "xp", 6));
        }
        assertTrue(tracker.getQuests().get(0).isComplete());
    }
    /**
     * THE GRADED PROOF of US-3.4: Combat publishes and has no reference to any
     listener.
     * If this fails, someone reached into Combat to add a feature that should have
     subscribed.
     */
    @Test
    void combatDoesNotDependOnAnyListener() throws Exception {
        String source = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Path.of("src/main/java/dungeonforge/core/Combat.java")));
        assertFalse(source.contains("QuestTracker"));
        assertFalse(source.contains("AchievementSystem"));
        assertFalse(source.contains("CombatLog"));
        assertFalse(source.contains("System.out"),
                "Combat must not print -- it publishes, and a view decides what to show");
    }
    // ---------- regression ----------
    @Test
    void earlierWeeksStillHold() {
        MonsterFactory factory = new MonsterFactory();
        assertEquals(19, factory.blueprintCount(), "Week 4's data loading");
        RandomSource.getInstance().reseed(99L);
        int first = factory.create("skeleton", 3).getMaxHp();
        RandomSource.getInstance().reseed(99L);
        int second = factory.create("skeleton", 3).getMaxHp();
        assertEquals(first, second, "Week 3's determinism");
    }
    @Test
    void addingAFifthStrategyRequiresNoChangeToMonsterOrCombat() {
        CombatStrategy cowardly = new CombatStrategy() {
            @Override public Action chooseAction(Monster s, Player t, Room r) {
                return Action.wait("does nothing at all");
            }
            @Override public String name() { return "inert"; }
        };
        Monster m = new Monster("Test", 10, 3, 5);
        m.setStrategy(cowardly);
        assertEquals(Action.Type.WAIT,
                m.getStrategy().chooseAction(m, new Player("P"), new
                        Room("r")).getType());
    }
}