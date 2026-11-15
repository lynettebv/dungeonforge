package dungeonforge.commands;

import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.Item;
import dungeonforge.items.Weapon;

public class DisenchantCommand extends AbstractCommand {


    public DisenchantCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        String target = args.trim();
        if (target.isEmpty()) {
            bus().message("Try disenchant <weapon name>");
            return false;
        }

        Item item = player().findItem(target);
        if (item == null) {
            bus().message("You are not carrying anything like that.");
            return false;
        }

        if (!(item instanceof Weapon w)) {
            bus().message("Only weapons can be disenchanted.");
            return false;
        }

        Weapon peeled = w.peel();
        if (peeled == w) {
            bus().message("The " + w.getName() + " has nothing on it to disenchant.");
            return false;
        }

        boolean wasEquipped = (player().getEquippedWeapon() == w);

        player().removeItem(w);
        player().addItem(peeled);
        if(wasEquipped) { player().equip(peeled); }
        bus().message("You stripped the enchantment away. It is now " + peeled.describe());
        return true;
    }

    @Override public String getDescription() { return ("disenchant " + args).trim(); }
}
