package dungeonforge.commands;

import dungeonforge.events.EventBus;

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

    @Override
    public void undo() { }
    @Override
    public String getDescription() { return "(nothing)"; }
    @Override
    public boolean isUndoable() { return false; }
    @Override
    public boolean endsTurn() { return false;}
}
