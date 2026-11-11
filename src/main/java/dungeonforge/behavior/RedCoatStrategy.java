package dungeonforge.behavior;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

import java.util.ArrayList;
import java.util.List;

public class RedCoatStrategy implements CombatStrategy {

    private double threshold = 0.95;

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        // If a player's HP falls below threshold this monster should switch sides.
        double startHP = GameConfig.getInstance().getDouble("playerStartingHp");
        if ((target.getHp()/startHP) > threshold ) return new Action(Action.Type.ATTACK, target,
                "strikes with a censer at " + target.getName());

        List<Monster> victims = new ArrayList<>();
        for (Monster m : room.getMonsters()) {
            if (m == self || !m.isAlive()) continue;
            victims.add(m);
        }
        Monster victim = RandomSource.getInstance().pick(victims);
        // If there is no victim, the Monster shows mercy and wits
        if (victim == null) return Action.wait(self.getName() + " takes pity and mocks you coldly.");

        // If a victim is found, the Monster becomes a traitor and helps you!
        return new Action(Action.Type.ATTACK, victim,
                "SWITCHES SIDES and strikes " + victim.getName() + " with and AXE");
    }

    @Override
    public String name() { return "redcoat"; }
}
