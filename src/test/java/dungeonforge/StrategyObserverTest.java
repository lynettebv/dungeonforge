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
        Action a = new HealerStrategy().chooseAction(healer, new Player("P"), room);
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
    /** The data file decides behavior, so a designer can retune it without a
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