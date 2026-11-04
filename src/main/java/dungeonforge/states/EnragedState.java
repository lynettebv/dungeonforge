package dungeonforge.states;

import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class EnragedState implements MonsterState {

    public static final double DAMAGE_BONUS = 1.5;

    @Override
    public void onEnter(Monster self) {
        self.setStrategy(new AggressiveStrategy());
    }

    @Override
    public boolean canAct() { return true; }

    @Override
    public double damageMultiplier() { return DAMAGE_BONUS; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();

        double flee = GameConfig.getInstance().getDouble("fleeThreshold");
        if (self.hpFraction() < flee) return new FleeingState();

        return this;
    }

    @Override
    public String label() { return "enraged"; }
}
