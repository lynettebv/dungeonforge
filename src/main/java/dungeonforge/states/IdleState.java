package dungeonforge.states;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class IdleState implements MonsterState {

    @Override public boolean canAct() { return false; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();
        return new HuntingState();
    }

    @Override public String label() { return "idle"; }
}
