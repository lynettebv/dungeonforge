package dungeonforge.states;

import java.util.List;

public class ExploringState implements GameState {
    private final static List<String> VERBS = List.of(
            "north", "south", "down", "look", "take", "loot", "drop", "use",
            "inventory", "pack", "equip", "enchant", "status", "quests", "history", "undo", "help", "quit");

    @Override
    public List<String> availableCommands() {
        return VERBS;
    }

    @Override public String name() { return "Exploring"; }
}
