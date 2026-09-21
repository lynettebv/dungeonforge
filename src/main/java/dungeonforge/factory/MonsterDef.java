package dungeonforge.factory;

public class MonsterDef {


    private final String id;
    private final String name;
    private final int hp;
    private final int attack;
    private final int xp;
    private final String theme;
    private final boolean boss;

    public MonsterDef(String id, String name, int hp, int attack,
        int xp, String theme) {
        this(id, name, hp, attack, xp, theme, false);
    }

    public MonsterDef(String id, String name, int hp, int attack,
                      int xp, String theme, boolean boss) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.xp = xp;
        this.theme = theme;
        this.boss = boss;
    }

    public String getId() {
        return id;
    }

    public boolean isBoss() {
        return boss;
    }

    public String getTheme() {
        return theme;
    }

    public int getXp() {
        return xp;
    }

    public int getAttack() {
        return attack;
    }

    public int getHp() {
        return hp;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " (" + name + " hp=" + hp + " attack=" + attack + " theme="
                + theme + (boss ? " BOSS " : "") +")";
    }
}
