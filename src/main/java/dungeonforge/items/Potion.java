package dungeonforge.items;

/** WEEK 4 -- a consumable. */
public class Potion implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int healAmount;

    public Potion(String name, double weight, int value, int healAmount) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.healAmount = healAmount;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getHealAmount()          { return healAmount; }

    @Override
    public String describe() {
        return name + " [heals " + healAmount + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}
