package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class GameOverState implements GameState {
    private final static List<String> VERBS = List.of(
            "quests", "status", "history", "quit" );


    @Override
    public void onEnter(GameContext ctx) {
        int count = ctx.getCurrentRoom().getMonsters().size();
        ctx.getBus().message(
                ctx.getPlayer().isAlive()
                        ? "=== YOU leave the dungeon behind === 'quests' for your record, or 'quit'."
                        : "=== YOU DIED === 'quests' to see how far you got, or 'quit'."
        );
    }

    @Override public List<String> availableCommands() { return VERBS; }
    @Override public String name() { return "Game Over"; }
}

