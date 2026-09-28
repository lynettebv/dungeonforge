package dungeonforge.behavior;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

public class RangedStrategy implements CombatStrategy{
    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        if(RandomSource.getInstance().nextDouble() < 0.25) {
            return Action.wait(self.getName() + "circles, looking for an angle");
        }
        return new Action(Action.Type.RANGED_ATTACK, target, self.getName() + "looses a shot");
    }

    @Override
    public String name() { return "ranged"; }
}
