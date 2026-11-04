package dungeonforge.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 7 -- MACRO COMMAND.
 *
 * "loot" is one word that becomes several TakeCommands. Note that this class holds
 * List<Command> and knows nothing about taking, moving or attacking -- it composes the
 * interface, not the implementations. Any command can go in a macro, including another macro.
 *
 * ==================== THE QUESTION TO SIT WITH ====================
 * undo() runs the children in REVERSE order. Work out why before you read on.
 *
 * Because each child captured its before-state at the moment IT ran, and those states are
 * nested like brackets. Undoing them forwards would restore an early snapshot and then have
 * a later snapshot overwrite it with the very state you were trying to escape.
 * ==================================================================
 */
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

    @Override public String getDescription() { return description; }
    @Override public boolean endsTurn()      { return false; }   // children each ended their own

    public int size() { return children.size(); }
}
