package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.*;

public class FrostThemeKit implements ThemeKit{
    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
            "Your breath hands in the air and does not disperse.",
            "Rime creeps across the floor toward your boots.",
            "It is silent here in a way that feels deliberate.",
            "Ice sheets the walls, and something moves behind it."
    };

    public FrostThemeKit(MonsterFactory factory) {
        this.factory = factory;
    }

    @Override
    public String themeName() {
        return "Frost";
    }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("frost"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("rime_tyrant", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch(RandomSource.getInstance().nextInt(4)) {
            case 0: return new Weapon("Rime Shard", 1.5, 50 + depth*11, 6 + depth);
            case 1: return new Armor("Frostweave Cloak", 2.0, 45 + depth*9, 3 + depth);
            case 2: return new Potion("Warming Cordial", 0.4, 28, 20 + depth * 3);
            default: return new Treasure("Frozen Tear",0.1, 90 + depth*20);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}
