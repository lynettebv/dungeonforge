package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

public class AggressiveStrategy implements CombatStrategy{

    @Override
    public Action chooseAction(Monster self, Player player, Room room) {
        return new Action(Action.Type.ATTACK, player, self.getName() + "lunges at you");
    }

    @Override
    public String name() { return "aggressive"; }
}
