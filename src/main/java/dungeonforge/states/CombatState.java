package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class CombatState implements GameState {
    private final static List<String> VERBS = List.of(
            "attack", "look", "use", "equip", "inventory", "status", "history", "undo", "help", "quit");


    @Override
    public void onEnter(GameContext ctx) {
        int count = ctx.getCurrentRoom().getMonsters().size();
        ctx.getBus().message("Combat! " + count + " hostile(s). Try 'attack'.");
    }

    @Override
    public void onExit(GameContext ctx) {
        ctx.getBus().message("The room falls quiet.");
    }

    @Override public List<String> availableCommands() {
        return VERBS;
    }
    @Override public String name() { return "Combat"; }
}
