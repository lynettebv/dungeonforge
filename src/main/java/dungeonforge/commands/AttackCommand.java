package dungeonforge.commands;

import dungeonforge.core.Monster;

/**
 * WEEK 7 -- COMMAND, and the one that teaches the hard half of undo.
 *
 * Attacking ends your turn, so the monsters strike back before control returns. Undoing this
 * command therefore has to reverse THE WHOLE EXCHANGE -- their damage, your XP, your gold,
 * and any monster that turned skittish or fled. See TurnSnapshot.
 */
public class AttackCommand extends AbstractCommand {

    public AttackCommand(GameContext ctx, String targetName) { super(ctx, targetName); }

    @Override
    protected boolean doExecute() {
        Monster target = findTarget();
        if (target == null) {
            bus().message(args.isBlank()
                    ? "There is nothing here to attack."
                    : "There is nothing here called '" + args + "'.");
            return false;
        }
        ctx.getCombat().playerStrikes(player(), target);
        return true;
    }

    private Monster findTarget() {
        if (args.isBlank()) return room().firstLivingIn();
        for (Monster m : room().getMonsters()) {
            if (m.isAlive() && m.getName().toLowerCase().contains(args.toLowerCase())) return m;
        }
        return null;
    }

    @Override public String getDescription() { return ("attack " + args).trim(); }
}
