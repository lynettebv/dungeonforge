package dungeonforge.items;

/**
 * WEEK 9 STARTER -- bites AND drinks. Two enchantments, one new class.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FrostVampiricWeapon extends Weapon {

    public FrostVampiricWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 3; }
    @Override public int getValue()   { return value + 45 + 90; }
    @Override public int lifesteal()  { return Math.max(1, (damage + 3) / 4); }

    @Override
    public String describe() {
        return "Frost Vampiric " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
