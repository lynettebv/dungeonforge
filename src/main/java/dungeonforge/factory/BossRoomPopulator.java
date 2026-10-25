package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.items.Chest;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD. One boss, one escort, one hoard.
 *
 * Notice that this class never names a monster. It asks the ThemeKit for "the boss of this
 * kind of place" and gets a Bone Tyrant in a crypt and a Rime Tyrant in the ice. Factory
 * Method decided HOW MANY; Abstract Factory decided WHICH.
 */
public class BossRoomPopulator extends RoomPopulator {

    public BossRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "boss"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        out.add(theme.createBoss(depth));
        out.add(theme.createMonster(depth));
        return out;
    }

    @Override
    protected Chest createChest(int depth) {
        Chest chest = new Chest("Tyrant's Hoard");
        chest.add(theme.createLoot(depth + 1));
        chest.add(theme.createLoot(depth + 1));
        return chest;
    }
}
