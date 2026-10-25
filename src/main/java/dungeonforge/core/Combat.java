package dungeonforge.core;

import dungeonforge.behavior.Action;
import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- strategies decide HOW a monster fights.
 * WEEK 7 -- one round at a time, because a human is typing.
 *
 * TODO(week 8, US-6.3): now look at what happened to this class.
 *
 * A monster has a LIFECYCLE -- idle, hunting, enraged, fleeing, dead -- and that lifecycle is
 * currently expressed as a String field plus conditionals in THREE separate places below:
 *
 *   updateMonsterState()  the transitions themselves, as nested ifs
 *   monsterActs()         "can this monster act at all?"
 *   damageFrom()          "is it enraged, and therefore hitting harder?"
 *
 * Chapter 10 of your textbook opens with exactly this design and then dismantles it. Before
 * you start, answer these in docs/evidence.md:
 *
 *   1. Add a "stunned" state that lasts one turn. How many methods below must you edit?
 *   2. Reading updateMonsterState(), can you tell at a glance which states can follow
 *      "enraged"? How long did it take you to be sure?
 *   3. What stops someone writing m.setStateName("dancing") somewhere and breaking everything?
 */
public class Combat {

    private final EventBus bus;

    public Combat(EventBus bus) { this.bus = bus; }

    /** One round of MONSTER actions. */
    public void monsterTurns(Player player, Room room) {
        for (Monster m : livingMonsters(room)) {
            updateMonsterState(m);
            monsterActs(m, player, room);
            if (!player.isAlive()) break;
        }
        if (!player.isAlive()) {
            bus.publish(GameEvent.of(EventType.PLAYER_DIED));
        } else if (!hasLiving(room)) {
            bus.publish(GameEvent.of(EventType.ROOM_CLEARED, "room", room.getId()));
        }
    }

    /**
     * TODO(week 8): PLACE 1 OF 3 -- the transitions, as nested conditionals.
     *
     * Every arrow in the monster's lifecycle is buried in here somewhere, and the ones that
     * are ABSENT (dead never becomes hunting) are invisible -- they are simply branches that
     * were never written.
     */
    private void updateMonsterState(Monster m) {
        double flee = GameConfig.getInstance().getDouble("fleeThreshold");
        double enrage = GameConfig.getInstance().getDouble("enrageThreshold");
        String current = m.getStateName();

        if (!m.isAlive()) {
            announce(m, current, "dead");
            m.setStateName("dead");
            return;
        }
        if (current.equals("idle")) {
            announce(m, current, "hunting");
            m.setStateName("hunting");
            return;
        }
        if (current.equals("hunting")) {
            if (m.hpFraction() < flee) {
                announce(m, current, "fleeing");
                m.setStateName("fleeing");
                m.setStrategy(new SkittishStrategy());
            } else if (m.hpFraction() < enrage) {
                announce(m, current, "enraged");
                m.setStateName("enraged");
                m.setStrategy(new AggressiveStrategy());
            }
            return;
        }
        if (current.equals("enraged")) {
            if (m.hpFraction() < flee) {
                announce(m, current, "fleeing");
                m.setStateName("fleeing");
                m.setStrategy(new SkittishStrategy());
            }
            return;
        }
        // "fleeing" and "dead" have no outbound transitions -- but you can only discover that
        // by noticing the absence of branches, which is not a great way to learn a state machine.
    }

    private void announce(Monster m, String from, String to) {
        if (from.equals(to)) return;
        bus.publish(GameEvent.of(EventType.STRATEGY_CHANGED,
                "name", m.getName(), "from", from, "to", to));
    }

    /** TODO(week 8): PLACE 2 OF 3 -- "may this monster act?", as a conditional. */
    private void monsterActs(Monster m, Player player, Room room) {
        String state = m.getStateName();
        if (state.equals("idle") || state.equals("dead")) return;
        if (m.getStrategy() == null) return;

        Action action = m.getStrategy().chooseAction(m, player, room);
        if (action == null) return;

        switch (action.getType()) {
            case ATTACK -> {
                int dmg = damageFrom(m);
                player.takeDamage(dmg);
                bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN,
                        "source", m.getName(), "amount", dmg, "flavor", action.getFlavor()));
            }
            case RANGED_ATTACK -> {
                int dmg = Math.max(1, (int) Math.round(damageFrom(m) * 0.8));
                player.takeDamage(dmg);
                bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN,
                        "source", m.getName(), "amount", dmg, "flavor", action.getFlavor()));
            }
            case FLEE -> {
                room.getMonsters().remove(m);
                bus.publish(GameEvent.of(EventType.MONSTER_FLED, "name", m.getName()));
            }
            case HEAL_ALLY -> {
                if (action.getTarget() != null) {
                    action.getTarget().heal(5);
                    bus.publish(GameEvent.of(EventType.MONSTER_HEALED,
                            "healer", m.getName(), "target", action.getTarget().getName()));
                }
            }
            case WAIT -> bus.publish(GameEvent.message(action.getFlavor()));
        }
    }

    /** TODO(week 8): PLACE 3 OF 3 -- "is it enraged?", as another conditional. */
    private int damageFrom(Monster m) {
        int base = m.getAttackPower();
        if (m.getStateName().equals("enraged")) {
            return (int) Math.round(base * 1.5);
        }
        return base;
    }

    public void playerStrikes(Player player, Monster target) {
        int damage = player.getAttackPower();
        target.takeDamage(damage);
        bus.publish(GameEvent.of(EventType.DAMAGE_DEALT,
                "target", target.getName(), "amount", damage));

        if (!target.isAlive()) {
            target.setStateName("dead");
            player.addXp(target.getXpReward());
            player.addGold(target.getXpReward() * 2);
            bus.publish(GameEvent.of(EventType.MONSTER_DIED,
                    "name", target.getName(), "xp", target.getXpReward()));
            bus.publish(GameEvent.of(EventType.XP_GAINED, "amount", target.getXpReward()));
            bus.publish(GameEvent.of(EventType.GOLD_GAINED, "amount", target.getXpReward() * 2));
        }
    }

    public Monster firstLivingIn(Room room) { return firstLiving(room); }

    private boolean hasLiving(Room room) { return firstLiving(room) != null; }

    private Monster firstLiving(Room room) {
        for (Monster m : room.getMonsters()) if (m.isAlive()) return m;
        return null;
    }

    private List<Monster> livingMonsters(Room room) {
        List<Monster> out = new ArrayList<>();
        for (Monster m : room.getMonsters()) if (m.isAlive()) out.add(m);
        return out;
    }

    /** Between rooms the player catches their breath. The dead do not catch their breath. */
    public static void restAfterRoom(Player player) {
        if (!player.isAlive()) return;
        player.heal(GameConfig.getInstance().getInt("restHealPerRoom"));
    }
}
