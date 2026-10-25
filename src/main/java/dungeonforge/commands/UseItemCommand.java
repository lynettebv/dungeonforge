package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;

/**
 * WEEK 7 -- COMMAND, and the home of US-5.4.
 *
 * The Chronomancer's Hourglass calls history.undoLast(). It is a game feature that exists
 * ONLY because actions are objects that remember their own before-state. In the Week 6
 * codebase it could not have been written at all -- there was nothing to rewind.
 *
 * That is the difference between a pattern being tidy and a pattern being load-bearing.
 */
public class UseItemCommand extends AbstractCommand {

    private boolean rewound;

    public UseItemCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    /** A rewind is not itself a turn, so this command's turn-ending-ness depends on what it did. */
    @Override public boolean endsTurn() { return !rewound; }

    @Override
    protected boolean doExecute() {
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You have nothing like that to use.");
            return false;
        }

        if (item instanceof Hourglass hourglass) {
            if (!hourglass.hasCharges()) {
                bus().message("The hourglass is spent. The sand will not run again.");
                return false;
            }
            hourglass.spendCharge();
            rewound = true;
            bus().message("Sand runs backwards. The last moment unhappens.");
            if (!ctx.getHistory().undoLast()) {
                bus().message("...but there was nothing to take back.");
            }
            return false;    // the rewind IS the effect; do not also burn a turn
        }

        if (item instanceof Potion potion) {
            player().heal(potion.getHealAmount());
            player().removeItem(item);
            bus().publish(GameEvent.message("You drink " + potion.getName()
                    + ". HP " + player().getHp() + "/" + player().getMaxHp()));
            return true;
        }

        bus().message("You cannot think of a use for " + item.getName() + " right now.");
        return false;
    }

    @Override public String getDescription() { return ("use " + args).trim(); }
}
