package dungeonforge.items;

/**
 * WEEK 9 STARTER -- burns AND bites. Two enchantments, one new class.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FlamingFrostWeapon extends Weapon {

    public FlamingFrostWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 4 + 3; }
    @Override public int getValue()   { return value + 60 + 45; }
    @Override public int lifesteal()  { return 0; }

    @Override
    public String describe() {
        return "Flaming Frost " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
