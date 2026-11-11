package dungeonforge.commands;

import dungeonforge.items.EnchantmentForge;
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

    public EnchantCommand(GameContext ctx, String args) { super(ctx, args); }

    @Override
    protected boolean doExecute() {
        // "slag cleaver with flaming"  ->  split on the LAST " with "
        int at = args.toLowerCase().lastIndexOf(" with ");
        if (at < 0) {
            bus().message("Try: enchant <weapon> with <flaming|frost|vampiric>");
            return false;
        }
        String target = args.substring(0, at).trim();
        String enchantment = args.substring(at + 6).trim();

        if (!EnchantmentForge.isKnown(enchantment)) {
            bus().message("No such enchantment: " + enchantment);
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

        Weapon result = EnchantmentForge.enchant(w, enchantment);
        if (result == null) {
            // THE MESSAGE THAT SHOULD NOT EXIST. There is nothing wrong with the request.
            // There is simply no class for the weapon it would produce.
            bus().message("That weapon cannot take another " + enchantment + " enchantment.");
            return false;
        }

        before = w;
        after = result;
        player().removeItem(w);
        player().addItem(result);
        bus().message("The " + w.getName() + " is now " + result.describe());

        // KNOWN DEFECT (US-7.2). `result` is a DIFFERENT OBJECT. If the old weapon was the
        // equipped one, the equipped slot still points at the unenchanted original, and the
        // player's damage does not change. Equip, enchant, then check `status` and see.
        return true;
    }

    @Override public String getDescription() { return ("enchant " + args).trim(); }
}
