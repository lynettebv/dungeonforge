package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class InventoryState implements GameState {
    private final static List<String> VERBS = List.of(
             "use", "inventory", "drop", "status", "back", "help", "quit");


    @Override
    public void onEnter(GameContext ctx) {
        ctx.getBus().message(" --- Your pack --- ('back' to close)");
    }


    @Override public List<String> availableCommands() { return VERBS; }
    @Override public String name() { return "Pack"; }
}
