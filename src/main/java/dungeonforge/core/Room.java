package dungeonforge.core;

import dungeonforge.config.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- one room of the dungeon.
 *
 * TODO(week 3, US-1.2): randomness source #2 of 3.
 */
public class Room {


    private static final String[] FLAVORS = {
        "Damp stone. Something drips in the dark, patiently.",
        "Burial niches line the walls. Most are empty. Most.",
        "The air tastes of old dust and older grief.",
        "Your footsteps come back a half-second late."
    };

    private final String id;
    private final String flavor;
    private final List<Monster> monsters = new ArrayList<>();

    public Room(String id) {
        this.id = id;
        this.flavor = RandomSource.getInstance().pick(FLAVORS);
    }

    public String getId()               { return id; }
    public String getFlavor()           { return flavor; }
    public List<Monster> getMonsters()  { return monsters; }
    public void addMonster(Monster m)   { monsters.add(m); }
}
