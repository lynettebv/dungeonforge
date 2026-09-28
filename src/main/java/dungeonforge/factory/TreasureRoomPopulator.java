package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Chest;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD, and the subclass that uses the HOOK.
 * One guard, and a chest worth guarding.
 */
public class TreasureRoomPopulator extends RoomPopulator {

    public TreasureRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "treasure"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        out.add(theme.createMonster(depth));
        return out;
    }

    /** HOOK OVERRIDE: two or three pieces of theme-appropriate loot. */
    @Override
    protected Chest createChest(int depth) {
        Chest chest = new Chest("Iron-bound Chest");
        int loot = RandomSource.getInstance().between(2, 3);
        for (int i = 0; i < loot; i++) {
            chest.add(theme.createLoot(depth));
        }
        return chest;
    }
}
