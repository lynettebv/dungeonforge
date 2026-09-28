package dungeonforge.core;

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
        if (room.getMonsters().isEmpty()) return true;

        System.out.println("    ! " + room.getMonsters().size() + " hostile(s)");

        int round = 0;
        while (player.isAlive() && hasLiving(room) && round++ < MAX_ROUNDS) {

            // --- player's turn: hit the first thing still standing ---
            Monster target = firstLiving(room);
            if (target != null) {
                int damage = player.getAttackPower();
                target.takeDamage(damage);
                System.out.println("      you hit " + target.getName() + " for " + damage);
                if (!target.isAlive()) {
                    System.out.println("      " + target.getName() + " dies");
                    player.addXp(target.getXpReward());
                    player.addGold(target.getXpReward() * 2);
                }
            }

            // --- monsters' turn ---
            for (Monster m : livingMonsters(room)) {
                monsterActs(m, player);
            }
        }
        return player.isAlive();
    }

    /**
     * TODO(week 5, US-3.1 and US-3.2): THIS METHOD IS THE WHOLE PROBLEM.
     *
     *  - every monster behaves identically, so species is cosmetic
     *  - adding a behaviour means editing this method
     *  - a monster cannot CHANGE tactics when it is badly wounded, because the behaviour
     *    is not a thing that can be swapped -- it is code baked into the encounter loop
     */
    private void monsterActs(Monster m, Player player) {
        int damage = m.getAttackPower();
        player.takeDamage(damage);
        System.out.println("      " + m.getName() + " hits you for " + damage);
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
