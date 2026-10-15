package dungeonforge.commands;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;

import java.util.Locale;

public class AttackCommand extends AbstractCommand {


    public AttackCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        Monster target = findTarget();
        if (target == null) {
            bus().message(args.isBlank() ?
                    "There is nothing here to attack." :
                    "There is nothing here called \"" + args + "\".");
            return false;
        }
        ctx.getCombat().playerStrikes(player(), target);
        return true;
    }

    private Monster findTarget() {
        if ( args.isBlank()) return room().firstLivingIn();
        for (Monster m : room().getMonsters()) {
            if (m.isAlive() && m.getName().toLowerCase().contains(args.toLowerCase())) {
                return m;
            }
        }
        return null;
    }

    @Override
    public String getDescription() { return ("attack " + args).trim(); }
}
