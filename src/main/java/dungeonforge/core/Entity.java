package dungeonforge.core;

/**
 * WEEK 1 -- the shared contract for anything that can fight.
 *
 * Nothing wrong with this class. It is here because Player and Monster extend it.
 */
public abstract class Entity {

    protected final String name;
    protected int hp;
    protected int maxHp;
    protected int attackPower;
    protected int defense;

    protected Entity(String name, int maxHp, int attackPower, int defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackPower = attackPower;
        this.defense = defense;
    }

    public String getName()      { return name; }
    public int getHp()           { return hp; }
    public int getMaxHp()        { return maxHp; }
    public int getAttackPower()  { return attackPower; }
    public int getDefense()      { return defense; }
    public boolean isAlive()     { return hp > 0; }

    public void takeDamage(int amount) { hp = Math.max(0, hp - Math.max(0, amount)); }
    public void heal(int amount)       { hp = Math.min(maxHp, hp + Math.max(0, amount)); }

    public abstract String describe();

    @Override public String toString() { return name; }
}
