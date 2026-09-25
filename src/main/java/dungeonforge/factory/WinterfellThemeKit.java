package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.*;

public class WinterfellThemeKit implements ThemeKit {
    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
            "The cold is biting at your nose. Direwolves glare from the forest edge.",
            "In the distance you see a pyre of smoke rising beyond the wall.",
            "The blue eyes of a White Walker peers into your soul like a dagger.",
            "A Direwolf chews on a bloody bone in the corner of the pub. The locals glare at you, the newcomer."
    };

    public WinterfellThemeKit(MonsterFactory factory) {
        this.factory = factory;
    }

    @Override
    public String themeName() {
        return "Winterfell";
    }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("winterfell"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("night_king", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch(RandomSource.getInstance().nextInt(4)) {
            case 0: return new Weapon("Valerian Steel GreatSword", 6.0, 80 + depth*10, 6 + depth);
            case 1: return new Armor("Direwolf pup", 2.0, 45 + depth*8, 5 + depth);
            case 2: return new Potion("Ice Magic", 0.4, 15, 10 + depth * 3);
            default: return new Treasure("Dragon Eggs", 0.5, 60 + depth*15);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}
