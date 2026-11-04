package dungeonforge.commands;

/**
 * WEEK 7 -- what lives in the parser's registry instead of an if-branch.
 *
 * A functional interface, so a registration is one lambda: the verb "north" maps to
 * (ctx, args) -> new MoveCommand(ctx, "north"). Week 4's Simple Factory idiom, at one line each.
 */
@FunctionalInterface
public interface CommandFactory {
    Command create(GameContext ctx, String args);
}
