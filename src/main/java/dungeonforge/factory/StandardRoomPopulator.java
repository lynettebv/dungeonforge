package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;

import java.util.ArrayList;
import java.util.List;

/** WEEK 4 -- FACTORY METHOD. Zero to N ordinary inhabitants, no chest. */
public class StandardRoomPopulator extends RoomPopulator {

    public StandardRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "standard"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        int max = GameConfig.getInstance().getInt("maxMonstersPerRoom");
        int count = RandomSource.getInstance().nextInt(max + 1);
        for (int i = 0; i < count; i++) {
            out.add(theme.createMonster(depth));
        }
        return out;
    }
}
