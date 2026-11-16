package dungeonforge.generate;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;
import dungeonforge.factory.*;

import java.util.*;

public abstract class LevelGenerator {

    public final DungeonLevel generate(int depth, ThemeKit theme, int roomCount, boolean isFinalLevel) {
        DungeonLevel level = new DungeonLevel(depth, theme.themeName());

        createRooms(level, depth, roomCount);
        connectRooms(level);
        markEntrance(level, theme);
        populateRooms(level, theme, depth, isFinalLevel);
        enforceReachability(level);

        return level;
    }

    protected abstract void connectRooms(DungeonLevel level);

    public abstract String shapeName();

    // Fixed Steps / Operations
    private void createRooms(DungeonLevel level, int depth, int roomCount) {
        for (int r = 0; r < roomCount; r++) level.addRoom(new Room("L" + depth + "R" + r));
    }
    private void markEntrance(DungeonLevel level, ThemeKit theme) {
        if (level.getRooms().isEmpty()) return;
        level.getRooms().get(0).setFlavor(theme.createRoomFlavor());
    }
    private void populateRooms(DungeonLevel level, ThemeKit theme, int depth, boolean isFinalLevel) {
        List<Room> rooms = level.getRooms();
        for (int r = 1; r < rooms.size(); r++) {
            boolean isBossRoom = isFinalLevel && ( r == rooms.size() - 1 );
            populatorFor(theme, isBossRoom).populate(rooms.get(r), depth);    }
    }

    private void enforceReachability(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        if (rooms.isEmpty()) return;

        Set<Room> seen = new HashSet<>();
        Deque<Room> queue = new ArrayDeque<>();
        queue.add(rooms.get(0));
        seen.add(rooms.get(0));
        while (!queue.isEmpty()) {
            for ( Room next : queue.poll().getExits().values()) {
                if (seen.add(next)) queue.add(next);
            }
        }
        rooms.removeIf(r -> !seen.contains(r));
    }
    private RoomPopulator populatorFor(ThemeKit theme, boolean isFinalRoom) {
        if (isFinalRoom) return new BossRoomPopulator(theme);
        if (RandomSource.getInstance().nextDouble() < 0.30) return new TreasureRoomPopulator(theme);
        return new StandardRoomPopulator(theme);
    }
}
