package dungeonforge.commands;

public interface Command {
    void execute();
    void undo();
    String getDescription();
    default boolean isUndoable() { return true; }
    default boolean endsTurn() { return true; }
}

