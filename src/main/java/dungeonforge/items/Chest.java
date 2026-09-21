package dungeonforge.items;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- a container of loot, placed by a TreasureRoomPopulator or BossRoomPopulator.
 *
 * Note it is NOT an Item. In Week 13 it becomes part of a COMPOSITE and that changes, but
 * building for that now would be guessing at a design you have not been taught yet.
 */
public class Chest {

    private final String name;
    private final List<Item> contents = new ArrayList<>();

    public Chest(String name) { this.name = name; }

    public String getName()        { return name; }
    public List<Item> getContents() { return contents; }
    public void add(Item item)     { contents.add(item); }
    public boolean isEmpty()       { return contents.isEmpty(); }

    public int totalValue() {
        int sum = 0;
        for (Item i : contents) sum += i.getValue();
        return sum;
    }
}
