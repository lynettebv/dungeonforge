package dungeonforge.commands;

/**
 * WEEK 7 -- COMMAND.
 *
 * "Encapsulate a request as an object, thereby letting you parameterize clients with
 *  different requests, queue or log requests, and support undoable operations."
 *
 * That definition lists four capabilities. Notice that we asked for exactly ONE of them
 * (undo) and the other three arrive whether we wanted them or not:
 *
 *   parameterize  -- an alias map and a hotkey bar both become one line of wiring
 *   queue         -- a macro is a list of Commands
 *   log           -- a replayable session log is just the list of descriptions
 *   undoable      -- the one we actually needed
 *
 * The reason all four come together is that they are the same capability wearing different
 * hats: once an action is an OBJECT rather than an if-branch, you can store it, copy it,
 * name it, put it in a list, and ask it to reverse itself. An if-branch is not a thing you
 * can keep.
 */
public interface Command {

    /** Do the thing. */
    void execute();

    /** Put the world back. Only meaningful if isUndoable() is true. */
    void undo();

    /** What appears in the replay log, e.g. "attack Skeleton". */
    String getDescription();

    /** Look, help and inventory change nothing, so they never go on the undo stack. */
    default boolean isUndoable() { return true; }

    /** Turn-ending commands let the monsters act afterwards. */
    default boolean endsTurn() { return true; }
}
