package dungeonforge.items;

/** WEEK 4 -- a weapon. Week 9 will decorate these with enchantments. */
public class Weapon implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int damage;

    public Weapon(String name, double weight, int value, int damage) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.damage = damage;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getDamage()              { return damage; }

    @Override
    public String describe() {
        return name + " [dmg " + damage + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}
