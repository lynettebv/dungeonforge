package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.*;

/** WEEK 4 -- ABSTRACT FACTORY. Cold, rime, things that were people. */
public class FrostThemeKit implements ThemeKit {

    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
        "Your breath hangs in the air and does not disperse.",
        "Rime creeps across the floor toward your boots.",
        "It is silent here in a way that feels deliberate.",
        "Ice sheets the walls, and something moves behind it."
    };

    public FrostThemeKit(MonsterFactory factory) { this.factory = factory; }

    @Override public String themeName() { return "Frost"; }

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
        switch (RandomSource.getInstance().nextInt(4)) {
            // WEEK 9 STARTER -- the theme decides which of the seven subclasses to name. A kit
            // should be able to say "fiery" without a class existing for every combination of
            // fiery with everything else. Right now it cannot.
            case 0:  return new FrostWeapon(new BasicWeapon("Rime Shard", 1.5, 50 + depth * 11, 6 + depth));
            case 1:  return new Armor("Frostweave Cloak", 2.0, 45 + depth * 9, 3 + depth);
            case 2:  return new Potion("Warming Cordial", 0.4, 28, 20 + depth * 3);
            default: return new Treasure("Frozen Tear", 0.1, 90 + depth * 20);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}
