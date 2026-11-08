package dungeonforge.items;

/**
 * WEEK 9 STARTER -- burns AND drinks. Two enchantments, one new class.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FlamingVampiricWeapon extends Weapon {

    public FlamingVampiricWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 4; }
    @Override public int getValue()   { return value + 60 + 90; }
    @Override public int lifesteal()  { return Math.max(1, (damage + 4) / 4); }

    @Override
    public String describe() {
        return "Flaming Vampiric " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
