package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY.
 *
 * Worth noticing: this strategy reads the ROOM, not just itself. A strategy may look at
 * anything it is handed. What it must not do is mutate the world -- it returns a decision and
 * Combat carries it out.
 */
public class HealerStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        Monster worst = null;
        for (Monster m : room.getMonsters()) {
            if (m == self || !m.isAlive()) continue;
            if (worst == null || m.hpFraction() < worst.hpFraction()) worst = m;
        }
        if (worst != null && worst.hpFraction() < 0.7) {
            return new Action(Action.Type.HEAL_ALLY, worst, self.getName() + " chants over " + worst.getName());
        }
        return new Action(Action.Type.ATTACK, target, self.getName() + " strikes with a censer");
    }

    @Override public String name() { return "healer"; }
}
