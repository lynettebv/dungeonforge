package dungeonforge.commands;

import dungeonforge.events.EventBus;

/**
 * WEEK 7 -- NULL OBJECT, a small bonus pattern Head First introduces alongside Command.
 *
 * The parser always returns a Command, never null. That means the game loop is three lines
 * with no null check, and "unknown verb" is handled by an object rather than by an if.
 *
 * Worth noticing: this is the same instinct as Week 5's decision that a strategy always
 * exists. Removing special cases is a recurring move, not a one-off trick.
 */
public class NoCommand implements Command {

    private final EventBus bus;
    private final String reason;

    public NoCommand(EventBus bus, String reason) {
        this.bus = bus;
        this.reason = reason;
    }

    @Override
    public void execute() {
        if (bus != null && reason != null && !reason.isBlank()) bus.message(reason);
    }

    @Override public void undo() { }
    @Override public String getDescription() { return "(nothing)"; }
    @Override public boolean isUndoable() { return false; }
    @Override public boolean endsTurn()   { return false; }
}
