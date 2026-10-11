package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;

public class UseItemCommand extends AbstractCommand {

    public UseItemCommand(GameContext ctx, String args) {
        super(ctx, args);
    }

    @Override
    protected boolean doExecute() {
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You've got nothing like that to use.");
            return false;
        }

        if (item instanceof Potion potion) {
            player().heal(potion.getHealAmount());
            player().removeItem(item);
            bus().publish(GameEvent.message("You drink " + potion.getName() + ". HP " +
                    " / " + player().getMaxHp()));
            return true;
        }

        bus().message("You cannot think of a use for " + item.getName() + " right now.");
        return false;
    }

    @Override
    public String getDescription() { return ("use " + args).trim(); }
}
