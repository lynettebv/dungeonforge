package dungeonforge.states;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public interface MonsterState {
    default void onEnter(Monster self) {}
    boolean canAct();
    default double damageMultiplier() {return 1.0; }
    MonsterState next(Monster self, Player player);
    String label();
}
