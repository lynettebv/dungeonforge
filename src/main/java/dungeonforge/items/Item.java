package dungeonforge.items;

/**
 * WEEK 4 -- introduced so that a ThemeKit has loot to hand back.
 *
 * Deliberately minimal. Week 9 will wrap these in DECORATORS to add enchantments, and
 * Week 13 will turn this into a COMPOSITE so bags can hold bags. Do not over-build it now.
 */
public interface Item {
    String getName();
    double getWeight();
    int getValue();

    default String describe() {
        return getName() + " (" + String.format("%.1f", getWeight()) + "kg, " + getValue() + "g)";
    }
}
