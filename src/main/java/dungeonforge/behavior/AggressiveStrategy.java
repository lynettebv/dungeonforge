package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/** WEEK 5 -- STRATEGY. Swings every turn, whatever the odds. */
public class AggressiveStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        return new Action(Action.Type.ATTACK, target, self.getName() + " lunges at you");
    }

    @Override public String name() { return "aggressive"; }
}
