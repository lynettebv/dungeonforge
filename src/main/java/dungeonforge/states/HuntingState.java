package dungeonforge.states;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class HuntingState implements MonsterState {

    @Override public boolean canAct() { return true; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();

        double flee = GameConfig.getInstance().getDouble("fleeThreshold");
        double enrage = GameConfig.getInstance().getDouble("enrageThreshold");

        if (self.hpFraction() < flee) return new FleeingState();
        if (self.hpFraction() < enrage) return new EnragedState();

        return this;
    }

    @Override public String label() { return "hunting"; }
}
