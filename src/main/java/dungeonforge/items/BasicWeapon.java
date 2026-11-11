package dungeonforge.items;

/**
 * WEEK 4 -- a weapon.
 * WEEK 9 STARTER -- and the root of this week's problem.
 *
 * Nothing is wrong with THIS class. The problem is the seven classes that extend it.
 * Open FlamingFrostVampiricWeapon and count how many times the fire arithmetic appears in
 * this package. Then work out what a FOURTH enchantment would cost.
 *
 * Two changes were needed to make the subclasses below possible, and both are dominoes:
 * the fields went from private to protected, and getDamage()/getValue()/describe() stopped
 * being things only this class decides.
 */
public class BasicWeapon implements Weapon {

    protected final String name;
    protected final double weight;
    protected final int value;
    protected final int damage;

    public BasicWeapon(String name, double weight, int value, int damage) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.damage = damage;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }

    public int getDamage() { return damage; }

    /**
     * WEEK 9 STARTER -- hit points restored to the wielder when this weapon lands a blow.
     * Zero for an ordinary weapon. Combat calls this after every player strike.
     */
    public int lifesteal() { return 0; }

    @Override
    public String describe() {
        return name + " [dmg " + getDamage() + "] (" + String.format("%.1f", weight) + "kg, " + getValue() + "g)";
    }
}
