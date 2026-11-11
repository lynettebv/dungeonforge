package dungeonforge.states;

import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class FleeingState implements MonsterState {
    public static final double DAMAGE_DEFICIT = 0.5;

    @Override
    public void onEnter(Monster self) {
        self.setStrategy(new SkittishStrategy());
    }

    @Override public boolean canAct() { return true; }

    @Override public double damageMultiplier() { return DAMAGE_DEFICIT; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();
        return this;
    }

    @Override public String label() { return "fleeing"; }
}
