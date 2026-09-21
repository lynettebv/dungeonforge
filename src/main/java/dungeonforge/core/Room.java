package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;
import dungeonforge.config.RandomSource;
import dungeonforge.items.Chest;
import dungeonforge.items.Item;

/**
 * WEEK 1 -- one room of the dungeon.
 * WEEK 3 (US-1.2) -- flavour text is picked from the one seeded source.
 */
public class Room {

    private static final String[] FLAVORS = {
        "Damp stone. Something drips in the dark, patiently.",
        "Burial niches line the walls. Most are empty. Most.",
        "The air tastes of old dust and older grief.",
        "Your footsteps come back a half-second late."
    };

    private final String id;
    private String flavor;
    private final List<Monster> monsters = new ArrayList<>();
    private final List<Item> floorItems = new ArrayList<>();
    private Chest chest;

    public Room(String id) {
        this.id = id;
        this.flavor = RandomSource.getInstance().pick(FLAVORS);
    }

    public String getId()               { return id; }
    public String getFlavor()           { return flavor; }
    public void setFlavor(String f)     { this.flavor = f; }
    public List<Monster> getMonsters()  { return monsters; }
    public void addMonster(Monster m)   { monsters.add(m); }
    public List<Item> getFloorItems()   { return floorItems; }
    public void addItem(Item i)         { floorItems.add(i); }
    public Chest getChest()             { return chest; }
    public void setChest(Chest c)       { this.chest = c; }
    public boolean hasChest()           { return chest != null && !chest.isEmpty(); }
}
