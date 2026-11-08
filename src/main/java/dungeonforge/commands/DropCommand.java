package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

/**
 * WEEK 7 -- COMMAND.
 *
 * Added last, and it took about four minutes: one class and one line in the parser's
 * registry. Compare that with adding a verb to the Week 6 if/else chain, and with what it
 * would have cost to give that version an undo.
 */
public class DropCommand extends AbstractCommand {

    public DropCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    @Override
    protected boolean doExecute() {
        // TODO(week 8, US-6.1): guard #3. MoveCommand and TakeCommand have one of these too.
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You are not carrying anything like that.");
            return false;
        }
        player().removeItem(item);
        room().getFloorItems().add(item);
        bus().publish(GameEvent.message("You drop " + item.getName() + "."));
        return true;
    }

    @Override public String getDescription() { return ("drop " + args).trim(); }
}
