package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that burns. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FlamingWeapon extends Weapon {

    public FlamingWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 4; }
    @Override public int getValue()   { return value + 60; }
    @Override public int lifesteal()  { return 0; }

    @Override
    public String describe() {
        return "Flaming " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
