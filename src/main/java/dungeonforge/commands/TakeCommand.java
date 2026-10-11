package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

public class TakeCommand extends AbstractCommand {

    public TakeCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        if (room().hasLivingMonsters()) {
            bus().message("Not while something is still moving in here.");
            return false;
        }
        Item found = takeFromFloor();
        if (found == null) { found = takeFromChest(); }

        if (found == null) {
            bus().message("There is nothing like that here!");
            return false;
        }

        player().addItem(found);
        bus().publish(GameEvent.message("You take " + found.getName() + "."));
        return true;
    }

    private Item takeFromFloor() {
        for (Item i : room().getFloorItems()) {
            if (matches(i)) {
                room().getFloorItems().remove(i); return i;
            }
        }
        return null;
    }

    private Item takeFromChest() {
        if (room().getChest() == null || room().getChest().getContents().isEmpty()) return null;

        for (Item i : room().getChest().getContents()) {
            if (matches(i)) { room().getChest().getContents().remove(i); return i; }
        }
        return null;
    }

    private boolean matches(Item i) {
        return args.isBlank() || i.getName().toLowerCase().contains(args.toLowerCase());
    }

    @Override
    public String getDescription() { return ("take " + args).trim(); }
}
