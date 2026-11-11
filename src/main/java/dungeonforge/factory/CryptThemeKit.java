package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.*;

/** WEEK 4 -- ABSTRACT FACTORY. Damp stone, the undead, bone charms. */
public class CryptThemeKit implements ThemeKit {

    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
        "Damp stone. Something drips in the dark, patiently.",
        "Burial niches line the walls. Most are empty. Most.",
        "The air tastes of old dust and older grief.",
        "Your footsteps come back a half-second late."
    };

    public CryptThemeKit(MonsterFactory factory) { this.factory = factory; }

    @Override public String themeName() { return "Crypt"; }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("crypt"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("bone_tyrant", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch (RandomSource.getInstance().nextInt(4)) {
            // WEEK 9 STARTER -- the theme decides which of the seven subclasses to name. A kit
            // should be able to say "fiery" without a class existing for every combination of
            // fiery with everything else. Right now it cannot.
            case 0:  return new VampiricWeapon(new BasicWeapon("Bone Shortsword", 2.0, 40 + depth * 10, 5 + depth));
            case 1:  return new Armor("Grave Shroud", 3.0, 35 + depth * 8, 2 + depth);
            case 2:  return new Potion("Vial of Still Water", 0.4, 25, 18 + depth * 3);
            default: return new Treasure("Bone Charm", 0.2, 60 + depth * 15);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}
