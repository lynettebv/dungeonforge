package dungeonforge.items;

/**
 * WEEK 9 STARTER -- all three. THIS is the class to open in Part A.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FlamingFrostVampiricWeapon extends Weapon {

    public FlamingFrostVampiricWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 4 + 3; }
    @Override public int getValue()   { return value + 60 + 45 + 90; }
    @Override public int lifesteal()  { return Math.max(1, (damage + 4 + 3) / 4); }

    @Override
    public String describe() {
        return "Flaming Frost Vampiric " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
