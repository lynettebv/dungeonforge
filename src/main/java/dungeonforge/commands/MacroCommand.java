package dungeonforge.commands;

import java.util.ArrayList;
import java.util.List;

public class MacroCommand implements Command {
    private final List<Command> children = new ArrayList<>();
    private final String description;

    public MacroCommand(String description, List<Command> children) {
        this.description = description;
        this.children.addAll(children);
    }

    @Override
    public void execute() {
        for (Command c : children) c.execute();
    }

    @Override
    public void undo() {
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).undo();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean endsTurn() {
        return false;
    }

    public int size() { return children.size(); }
}
