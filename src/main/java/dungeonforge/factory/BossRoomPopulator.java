package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Chest;

import java.util.ArrayList;
import java.util.List;

public class BossRoomPopulator extends RoomPopulator {

    public BossRoomPopulator(ThemeKit theme) { super(theme); }

    @Override
    public String kind() {
        return "Boss";
    }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        out.add(theme.createBoss(depth));
        out.add(theme.createMonster(depth));
        return out;
    }

    @Override
    protected Chest createChest(int depth) {
        Chest chest = new Chest( "Tyrant's Hoard");
        chest.add(theme.createLoot(depth +1));
        chest.add(theme.createLoot(depth +1));
        return chest;
    }

}
