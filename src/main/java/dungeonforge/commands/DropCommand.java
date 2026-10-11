package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

public class DropCommand extends AbstractCommand {

    public DropCommand(GameContext ctx, String args) {
        super(ctx, args);
    }

    @Override
    protected boolean doExecute() {
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You're not carrying anything like that, buddy.");
            return false;
        }
        player().removeItem(item);
        room().getFloorItems().add(item);
        bus().publish(GameEvent.message("You drop " + item.getName() + "."));
        return true;
    }

    @Override
    public String getDescription() { return ("drop " + args).trim(); }
}
