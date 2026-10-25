package dungeonforge.behavior;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY, and the one that makes the pattern click.
 *
 * Combat SWAPS a badly wounded monster to this strategy mid-fight. The monster object does
 * not change class, does not get rebuilt, and keeps its hit points and its name -- it simply
 * starts making different decisions. That is only possible because behaviour is an object it
 * HAS rather than something it IS.
 */
public class SkittishStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        double threshold = GameConfig.getInstance().getDouble("fleeThreshold");
        if (self.hpFraction() < threshold) {
            return new Action(Action.Type.FLEE, null, self.getName() + " scrabbles backwards");
        }
        return new Action(Action.Type.ATTACK, target, self.getName() + " snaps at you nervously");
    }

    @Override public String name() { return "skittish"; }
}
