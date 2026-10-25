package dungeonforge.commands;

/**
 * WEEK 7 -- a read-only COMMAND for look, help, inventory, quests.
 *
 * These change nothing, so they are never undoable and never end a turn. Keeping them as
 * Commands anyway means the parser has exactly one return type and the game loop has exactly
 * one shape. Special cases are what we are trying to delete.
 */
public class SimpleCommand implements Command {

    /** A no-argument, no-return action. Java's Runnable would also do; this reads better here. */
    public interface Body { void run(); }

    private final String description;
    private final Body body;

    public SimpleCommand(String description, Body body) {
        this.description = description;
        this.body = body;
    }

    @Override public void execute()          { body.run(); }
    @Override public void undo()             { }
    @Override public String getDescription() { return description; }
    @Override public boolean isUndoable()    { return false; }
    @Override public boolean endsTurn()      { return false; }
}
