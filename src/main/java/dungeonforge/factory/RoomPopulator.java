package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.items.Chest;

import java.util.List;

public abstract class RoomPopulator {

    protected final ThemeKit theme;

    protected RoomPopulator(ThemeKit theme) { this.theme = theme; }

    public final void populate(Room room, int depth) {
        room.setFlavor(theme.createRoomFlavor());

        for(Monster m : createEncounter(depth)) {
            room.addMonster(m);
        }

        Chest chest = createChest(depth);
        if( chest != null && !chest.isEmpty()) {
            room.setChest(chest);
        }
    }

    protected abstract List<Monster> createEncounter(int depth);

    protected Chest createChest(int depth) {
        return null;
    }

    public abstract String kind();
}
