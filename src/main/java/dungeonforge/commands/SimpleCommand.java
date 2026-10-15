package dungeonforge.commands;

public class SimpleCommand implements Command {

    public interface Body { void run(); }

    private final String description;
    private final Body body;

    public SimpleCommand(String description, Body body) {
        this.description = description;
        this.body = body;
    }

    @Override
    public void execute() { body.run(); }
    @Override
    public void undo() { }
    @Override
    public String getDescription() { return description; }

    @Override
    public boolean isUndoable() { return false; }
    @Override
    public boolean endsTurn() { return false; }
}
