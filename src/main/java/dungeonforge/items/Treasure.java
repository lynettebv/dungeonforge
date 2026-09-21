package dungeonforge.items;

/** WEEK 4 -- pure value, no combat effect. */
public class Treasure implements Item {

    private final String name;
    private final double weight;
    private final int value;

    public Treasure(String name, double weight, int value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
}
