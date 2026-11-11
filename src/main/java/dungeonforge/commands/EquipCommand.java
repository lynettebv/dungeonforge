package dungeonforge.commands;

import dungeonforge.items.Item;
import dungeonforge.items.Weapon;

/**
 * WEEK 9 (PROVIDED) -- the command you costed out in Week 7's evidence sheet.
 *
 * Compare this file with the 12th `else if` you sketched back then. The logic is the same
 * logic. What changed is that it has a name, a file, and an undo().
 *
 * Note there is no undo() here: TurnSnapshot already restores the inventory, and Week 9's
 * US-7.2 teaches it the hand as well. The inherited one is correct.
 */
public class EquipCommand extends AbstractCommand {

    public EquipCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        if (args.isBlank()) {
            Weapon held = player().getEquippedWeapon();
            bus().message(held == null ? "You are unarmed." : "You are wielding " + held.describe());
            return false;
        }
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You are not carrying anything like that.");
            return false;
        }
        if (!(item instanceof Weapon w)) {
            bus().message("You cannot equip " + item.getName() + ".");
            return false;
        }
        Weapon previous = player().equip(w);
        bus().message("You ready the " + w.describe()
                + (previous == null ? "." : ", stowing the " + previous.getName() + "."));
        return true;
    }

    @Override public String getDescription() { return ("equip " + args).trim(); }
}
