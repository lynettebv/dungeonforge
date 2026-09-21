package dungeonforge.items;

/** WEEK 4 -- armour. */
public class Armor implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int defense;

    public Armor(String name, double weight, int value, int defense) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.defense = defense;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getDefense()             { return defense; }

    @Override
    public String describe() {
        return name + " [def " + defense + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}
