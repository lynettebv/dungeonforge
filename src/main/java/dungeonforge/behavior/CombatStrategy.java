package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

public interface CombatStrategy {
    Action chooseAction(Monster self, Player player, Room room);

    /** For logs and tests */
    String name();
}
