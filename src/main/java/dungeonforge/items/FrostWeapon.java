package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that bites with cold. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FrostWeapon extends Weapon {

    public FrostWeapon(String name, double weight, int value, int damage) {
        super(name, weight, value, damage);
    }

    @Override public int getDamage()  { return damage + 3; }
    @Override public int getValue()   { return value + 45; }
    @Override public int lifesteal()  { return 0; }

    @Override
    public String describe() {
        return "Frost " + name + " [dmg " + getDamage() + "]"
                + " (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
