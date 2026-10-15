package dungeonforge.commands;

public interface CommandFactory {
    Command create (GameContext ctx, String args);
}
