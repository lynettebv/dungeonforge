package dungeonforge.core;

import dungeonforge.config.GameConfig;

/**
 * WEEK 1 -- the player.
 *
 * TODO(week 3, US-1.1): those four numbers are hardcoded. A game designer who wants to
 * rebalance the game has to edit Java and recompile. Find them all -- they are not only
 * in this file.
 */
public class Player extends Entity {

    private int gold;
    private int xp;

    public Player(String name) {
        // Starting HP, attack, defense. Hardcoded. This is one of the things US-1.1 is about.
        super(name,
                GameConfig.getInstance().getInt("playerStartingHp"),
                GameConfig.getInstance().getInt("playerStartingAttack"),
                GameConfig.getInstance().getInt("playerStartingDefense"));
    }

    public int getGold()          { return gold; }
    public int getXp()            { return xp; }
    public void addGold(int g)    { gold += g; }
    public void addXp(int x)      { xp += x; }

    /** Backpack capacity in kilograms. Also hardcoded. */
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
