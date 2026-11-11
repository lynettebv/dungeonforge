package dungeonforge.commands;

import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.Item;
import dungeonforge.items.Weapon;

/**
 * WEEK 9 STARTER -- PART OF THE PROBLEM.
 *
 * `enchant <weapon> with <enchantment>`
 *
 * This command is honest about what it can do, which is not much. Read the three failure
 * messages below -- each one is a limitation of EnchantmentForge leaking into the player's
 * face. None of them is a rule anyone chose. They are all "we ran out of classes."
 *
 * US-7.2 makes every one of these messages impossible to reach.
 */
public class EnchantCommand extends AbstractCommand {

    private Weapon before;
    private Weapon after;
    private boolean wasEquipped;

    public EnchantCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        // "slag cleaver with flaming"  ->  split on the LAST " with "
        int at = args.toLowerCase().lastIndexOf(" with ");
        if (at < 0) {
            bus().message("Try: enchant <weapon> with <" + String.join("|", EnchantmentRegistry.known()) + ">");
            return false;
        }
        String target = args.substring(0, at).trim();
        String enchantment = args.substring(at + 6).trim();

        if (!EnchantmentRegistry.isKnown(enchantment)) {
            bus().message("No such enchantment: " + enchantment + ". Known: "
            + String.join("|", EnchantmentRegistry.known()));
            return false;
        }
        Item item = player().findItem(target);
        if (item == null) {
            bus().message("You are not carrying anything like that.");
            return false;
        }
        if (!(item instanceof Weapon w)) {
            bus().message("Only weapons take enchantments.");
            return false;
        }

        before = w;
        after = EnchantmentRegistry.apply(enchantment, w);
        wasEquipped = (player().getEquippedWeapon() == w);

        player().removeItem(w);
        player().addItem(after);
        if(wasEquipped) { player().equip(after); }
        bus().message("The " + w.getName() + " is now " + after.describe());

        // KNOWN DEFECT (US-7.2). `result` is a DIFFERENT OBJECT. If the old weapon was the
        // equipped one, the equipped slot still points at the unenchanted original, and the
        // player's damage does not change. Equip, enchant, then check `status` and see.
        return true;
    }

    @Override public String getDescription() { return ("enchant " + args).trim(); }
}
