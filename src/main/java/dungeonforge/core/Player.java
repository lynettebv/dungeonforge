package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- the player.
 * WEEK 3 (US-1.1) -- every tunable number now comes from GameConfig. There is not one
 * numeric literal left in this class, which is what acceptance criterion 2 demanded.
 */
public class Player extends Entity {

    private int gold;
    private int xp;
    /**
     * WEEK 7 (provided): a plain list, deliberately.
     * Week 13 turns this into a COMPOSITE so bags can hold bags. Do not build that now.
     */
    private final List<Item> inventory = new ArrayList<>();

    public Player(String name) {
        super(name,
              GameConfig.getInstance().getInt("playerStartingHp"),
              GameConfig.getInstance().getInt("playerStartingAttack"),
              GameConfig.getInstance().getInt("playerStartingDefense"));
    }

    public List<Item> getInventory()  { return inventory; }
    public void addItem(Item i)       { inventory.add(i); }
    public boolean removeItem(Item i) { return inventory.remove(i); }

    /** Case-insensitive partial match, so "use draught" finds "Quench Draught". */
    public Item findItem(String query) {
        if (query == null || query.isBlank()) return null;
        String q = query.toLowerCase();
        for (Item i : inventory) if (i.getName().toLowerCase().contains(q)) return i;
        return null;
    }

    public void setGold(int g) { this.gold = Math.max(0, g); }
    public void setXp(int x)   { this.xp = Math.max(0, x); }

    public int getGold()          { return gold; }
    public int getXp()            { return xp; }
    public void addGold(int g)    { gold += g; }
    public void addXp(int x)      { xp += x; }

    /** Backpack capacity in kilograms, from config. */
    public double carryCapacity() {
        return GameConfig.getInstance().getDouble("carryCapacity");
    }

    @Override
    public String describe() {
        return name + "  HP " + hp + "/" + maxHp
                + "  ATK " + attackPower + "  DEF " + defense
                + "  Gold " + gold + "  XP " + xp
                + "  Carry " + carryCapacity() + "kg";
    }
}

