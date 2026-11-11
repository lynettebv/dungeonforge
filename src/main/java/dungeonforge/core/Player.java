package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.items.Item;
import dungeonforge.items.Weapon;

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

    /**
     * WEEK 9 (PROVIDED) -- the equipped weapon.
     *
     * In Week 7 you were asked, in docs/evidence.md, to list every edit that adding `equip`
     * would cost. Here is that bill, paid, so you can check your prediction:
     *
     *   Player      -- this field, equip(), getEquippedWeapon(), the getAttackPower()
     *                  override below, and describe()          <- the FEATURE. Unavoidable.
     *   Combat      -- ONE line, for lifesteal. The damage calculation did not change at all,
     *                  because the override answers the question Combat already asked. That
     *                  is Dependency Inversion paying rent.
     *   EquipCommand + one register() + one alias()             <- the WIRING. Two lines.
     *
     * The wiring shrank. The feature did not. That is exactly what Week 7 promised, and it is
     * worth re-reading your own answer now that you can see the invoice.
     */
    private Weapon equippedWeapon;

    public Weapon getEquippedWeapon() { return equippedWeapon; }

    /** Returns whatever was in the hand before, so the caller can put it away. */
    public Weapon equip(Weapon w) {
        Weapon previous = this.equippedWeapon;
        this.equippedWeapon = w;
        return previous;
    }

    public Weapon unequip() {
        Weapon previous = this.equippedWeapon;
        this.equippedWeapon = null;
        return previous;
    }

    /**
     * WEEK 9 (PROVIDED) -- Combat already asks every Entity how hard it hits. It keeps asking;
     * the Player just gives a better answer now. Not one line of the damage calculation moved.
     */
    @Override
    public int getAttackPower() {
        return attackPower + (equippedWeapon == null ? 0 : equippedWeapon.getDamage());
    }

    /** Hit points the equipped weapon gives back on a landed blow. Zero for bare hands. */
    public int lifesteal() {
        return equippedWeapon == null ? 0 : equippedWeapon.lifesteal();
    }

    public List<Item> getInventory()   { return inventory; }
    public void addItem(Item i)        { inventory.add(i); }
    public boolean removeItem(Item i)  { return inventory.remove(i); }

    /** Case-insensitive partial match, so "use draught" finds "Quench Draught". */
    public Item findItem(String query) {
        if (query == null || query.isBlank()) return null;
        String q = query.toLowerCase();
        for (Item i : inventory) {
            if (i.getName().toLowerCase().contains(q)) return i;
        }
        return null;
    }

    public void setGold(int g)    { this.gold = Math.max(0, g); }
    public void setXp(int x)      { this.xp = Math.max(0, x); }

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
        // WEEK 9 (PROVIDED) -- getAttackPower(), not the raw field. Reading the field here was
        // a real bug waiting to happen: the override above would never reach this line, and
        // `status` would keep reporting your unarmed damage forever. Call your own accessors.
        return name + "  HP " + hp + "/" + maxHp
                + "  ATK " + getAttackPower() + "  DEF " + defense
                + "  Gold " + gold + "  XP " + xp
                + "  Carry " + carryCapacity() + "kg"
                + (equippedWeapon == null ? "  (unarmed)" : "  wielding " + equippedWeapon.describe());
    }
}
