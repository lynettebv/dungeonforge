package dungeonforge.core;

import dungeonforge.behavior.Action;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.config.GameConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- a minimal encounter resolver. New this week so that monsters have something to
 * DO. Week 7 turns player actions into Command objects and Week 10 replaces this loop with a
 * Template Method, so do not polish it -- it is scaffolding.
 *
 * TODO(week 5, US-3.1): look at monsterActs(). Every monster in the game fights the same way,
 * because "how a monster fights" is an if/else chain living inside this class. A Skeleton and
 * an Imp are indistinguishable in a fight.
 *
 * The obvious fix is to subclass Monster -- AggressiveMonster, RangedMonster, SkittishMonster.
 * Before you do that, count: we have 15 species and we want 4 behaviours. Do the arithmetic
 * in docs/strategy-observer-clinic.md Part D1 BEFORE you write any code.
 *
 * TODO(week 5, US-3.3): look at everything this class prints, and at the xp/gold bookkeeping.
 * Combat knows about the console. If we want quests, achievements or a scrolling log, they
 * all have to be bolted into this class, and it grows forever.
 */
public class Combat {

    private static final int MAX_ROUNDS = 40;

    /** Returns true if the player survived the encounter. */
    public boolean fight(Player player, Room room, int depth) {
        if (!hasLiving(room)) return true;

        System.out.println("    ! " + room.getMonsters().size() + " hostile(s)");

        int round = 0;
        while (player.isAlive() && hasLiving(room) && round++ < MAX_ROUNDS) {
            playerActs(player, room);
            if (!hasLiving(room)) break;

            // --- monsters' turn ---
            for (Monster m : livingMonsters(room)) {
                checkForTacticsChange(m);
                monsterActs(m, player, room);
                if(!player.isAlive()) break;
            }
        }
        if (!player.isAlive()) {
            // TODO: publish with observer event
            System.out.println("Game Over - Player Died");
            return false;
        }
        // TODO: publish with observer event
        System.out.println("Player Survived - Room Cleared");
        return true;
    }

    private void playerActs(Player player, Room room) {
        // --- player's turn: hit the first thing still standing ---
        Monster target = firstLiving(room);
        if (target == null) return;

        int damage = player.getAttackPower();
        target.takeDamage(damage);
        // TODO: publish with observer event
        System.out.println("      you hit " + target.getName() + " for " + damage);
        if (!target.isAlive()) {
            player.addXp(target.getXpReward());
            player.addGold(target.getXpReward() * 2);
            // TODO: publish with observer event
            System.out.println("      " + target.getName() + " dies");
        }
    }

    private void checkForTacticsChange(Monster m) {
        double threshold = GameConfig.getInstance().getDouble("fleeThreshold");
        if (m.hpFraction() >= threshold) return;
        if (m.getStrategy() instanceof SkittishStrategy) return;

        String from = m.getStrategy().name();
        m.setStrategy(new SkittishStrategy());
        // TODO: publish this event once Observer Pattern set.
        System.out.println(m.getName() + " changed strategy from " + from + " to " + m.getStrategy().name());
    }

    /**
     * TODO(week 5, US-3.1 and US-3.2): THIS METHOD IS THE WHOLE PROBLEM.
     *
     *  - every monster behaves identically, so species is cosmetic
     *  - adding a behaviour means editing this method
     *  - a monster cannot CHANGE tactics when it is badly wounded, because the behaviour
     *    is not a thing that can be swapped -- it is code baked into the encounter loop
     */
    private void monsterActs(Monster m, Player player, Room room) {
        if(m.getStrategy() == null) return;
        Action action = m.getStrategy().chooseAction(m, player, room);
        if( action == null) return;

        switch (action.getType()) {
            case ATTACK -> {
                int dmg = m.getAttackPower();
                player.takeDamage(dmg);
            }
            case RANGED_ATTACK -> {
                int dmg = Math.max(1, (int)Math.round(m.getAttackPower() * 0.8));
                player.takeDamage(dmg);
            }
            case FLEE -> {
                room.getMonsters().remove(m);
            }
            case HEAL_ALLY -> {
                if(action.getTarget() != null) {
                    action.getTarget().heal(5);
                }
            }
            case WAIT -> {}
        }
    }

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

    /** Between rooms the player catches their breath. Tunable, so it lives in config. */
    /** Between rooms the player catches their breath. The dead do not catch their breath. */
    public static void restAfterRoom(Player player) {
        if (!player.isAlive()) return;
        player.heal(GameConfig.getInstance().getInt("restHealPerRoom"));
    }
}
