package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that drinks. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class VampiricWeapon extends Weapon {

    public VampiricWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage; }
    @Override public int getValue()   { return value + 90; }
    @Override public int lifesteal()  { return Math.max(1, damage / 4); }

    @Override
    public String describe() {
        return "Vampiric " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
