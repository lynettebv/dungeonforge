package dungeonforge.commands;

import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.EventBus;

/**
 * WEEK 7 -- shared plumbing for COMMAND.
 *
 * Note the shape of execute(): it is FINAL and it defines the order of a turn --
 * snapshot, act, and if the action really happened, let the monsters respond.
 * Subclasses fill in doExecute() only.
 *
 * If that feels familiar, it should: you are looking at a Template Method three weeks before
 * Week 10 formally teaches it. Patterns do not arrive in the order a textbook lists them.
 */
public abstract class AbstractCommand implements Command {

    protected final GameContext ctx;
    protected final String args;

    private TurnSnapshot before;
    private boolean actuallyRan;

    protected AbstractCommand(GameContext ctx, String args) {
        this.ctx = ctx;
        this.args = args == null ? "" : args.trim();
    }

    protected Player player()  { return ctx.getPlayer(); }
    protected Room room()      { return ctx.getCurrentRoom(); }
    protected EventBus bus()   { return ctx.getBus(); }

    @Override
    public final void execute() {
        if (endsTurn()) {
            before = TurnSnapshot.capture(ctx.getPlayer(), ctx.getCurrentRoom());
        }

        actuallyRan = doExecute();

        if (!actuallyRan) {
            before = null;              // a refused command does not go on the undo stack
            return;
        }
        if (endsTurn()) {
            ctx.runMonsterTurns();
        }
    }

    /**
     * Returns FALSE if the command could not run -- no such exit, nothing to attack.
     * A refused command must not burn a turn and must not be undoable, which is why this
     * returns a boolean instead of void.
     */
    protected abstract boolean doExecute();

    @Override
    public void undo() {
        if (before != null) {
            before.restore();
            ctx.setCurrentRoom(before.getRoom());
        }
    }

    /**
     * A command that was REFUSED must not go on the undo stack.
     *
     * "There is no way south" is not a move you should be able to take back -- and if it went
     * on the stack, the player's next undo would silently do nothing while consuming their
     * hourglass charge. This is the kind of bug that only shows up once undo exists, which is
     * why it is worth stating in code rather than in a comment.
     */
    @Override
    public boolean isUndoable() { return actuallyRan; }

    public boolean didRun() { return actuallyRan; }
}
