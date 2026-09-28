package dungeonforge.behavior;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

public class SkittishStrategy implements CombatStrategy {
    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        double threshold = GameConfig.getInstance().getDouble("fleeThreshold");
        if(self.hpFraction() < threshold) {
            return new Action(Action.Type.FLEE, null, self.getName() + "scrabbles backwards");
        }
        return new Action(Action.Type.ATTACK, target, self.getName() + "snaps at you nervously");
    }

    @Override
    public String name() {
        return "skittish";
    }
}
