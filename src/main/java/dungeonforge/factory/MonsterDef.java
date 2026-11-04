package dungeonforge.factory;

/**
 * WEEK 4 -- the blueprint a factory turns into a Monster.
 *
 * Plain data, no behaviour. Its whole job is to let monster CONTENT live in a JSON file
 * instead of in Java, so adding a monster is a data change rather than a code change.
 */
public class MonsterDef {

    public final String id;
    public final String name;
    public final int hp;
    public final int attack;
    public final int xp;
    public final String theme;
    /** Bosses are excluded from ordinary spawns -- a ThemeKit asks for one by name. */
    public final boolean boss;
    /** WEEK 5: which CombatStrategy this species uses by default. Content, not code. */
    public final String strategy;

    public MonsterDef(String id, String name, int hp, int attack, int xp, String theme) {
        this(id, name, hp, attack, xp, theme, false, "aggressive");
    }

    public MonsterDef(String id, String name, int hp, int attack, int xp, String theme,
                      boolean boss, String strategy) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.xp = xp;
        this.theme = theme;
        this.boss = boss;
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return id + "(" + name + " hp=" + hp + " atk=" + attack + " theme=" + theme
                + (boss ? " BOSS" : "") + ")";
    }
}
