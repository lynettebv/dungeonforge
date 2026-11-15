package dungeonforge.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WEEK 10 (PROVIDED) -- a diagnostic, not a feature.
 *
 * `mvn -q exec:java -Dexec.args="--report"` prints one row per level and then exits. Use it in
 * Part 0 to measure the problem and again in Part D to prove you fixed it.
 *
 * It checks three things that ought to be true of every level, whatever shape it is:
 *
 *   ORPHANS  -- rooms with no path from the entrance. Should always be 0.
 *   SPAWN    -- monsters in the room the player starts in. Should always be 0.
 *   EXIT     -- can you reach the stairs down from the entrance? Should always be yes.
 *
 * Nothing in this class knows how a level is built. It only knows what a finished level is
 * supposed to look like -- which is exactly why it can catch a builder that skipped a step.
 */
public final class DungeonReport {

    private DungeonReport() { }

    public static String of(GameWorld world) {
        StringBuilder b = new StringBuilder();
        b.append("\n  DUNGEON REPORT\n");
        b.append("  ------------------------------------------------------------------\n");
        b.append(String.format("  %-6s %-12s %6s %8s %7s %6s%n",
                "LEVEL", "THEME", "ROOMS", "ORPHANS", "SPAWN", "EXIT"));
        b.append("  ------------------------------------------------------------------\n");

        int totalOrphans = 0, totalSpawn = 0, missingExits = 0;
        List<DungeonLevel> levels = world.getLevels();

        for (int i = 0; i < levels.size(); i++) {
            DungeonLevel level = levels.get(i);
            List<Room> rooms = level.getRooms();

            int orphans = countOrphans(level);
            int spawn = rooms.isEmpty() ? 0 : rooms.get(0).getMonsters().size();
            boolean lastLevel = (i == levels.size() - 1);
            boolean exitOk = lastLevel || stairsReachable(level);

            totalOrphans += orphans;
            totalSpawn += spawn;
            if (!exitOk) missingExits++;

            b.append(String.format("  %-6d %-12s %6d %8s %7s %6s%n",
                    level.getDepth(), level.getThemeName(), rooms.size(),
                    orphans == 0 ? "0" : orphans + " !!",
                    spawn == 0 ? "0" : spawn + " !!",
                    lastLevel ? "n/a" : (exitOk ? "yes" : "NO !!")));
        }

        b.append("  ------------------------------------------------------------------\n");
        b.append(String.format("  orphaned rooms: %d   spawn-camped levels: %d   unreachable exits: %d%n",
                totalOrphans, totalSpawn > 0 ? 1 : 0, missingExits));

        boolean clean = totalOrphans == 0 && totalSpawn == 0 && missingExits == 0;
        b.append(clean
                ? "  every level satisfies every invariant.\n"
                : "  AT LEAST ONE LEVEL IS BROKEN. Which builder produced it, and what did it skip?\n");
        return b.toString();
    }

    /** Rooms on this level that the entrance cannot reach. */
    public static int countOrphans(DungeonLevel level) {
        if (level.getRooms().isEmpty()) return 0;
        return level.getRooms().size() - reachableWithin(level).size();
    }

    /** Is the room holding the "down" staircase reachable from the entrance? */
    public static boolean stairsReachable(DungeonLevel level) {
        if (level.getRooms().isEmpty()) return false;
        for (Room r : reachableWithin(level)) {
            if (r.getExits().containsKey("down")) return true;
        }
        return false;
    }

    /**
     * Breadth-first from the entrance, but STAYING ON THIS LEVEL.
     *
     * The bound matters. Without it the search walks through the staircase into the next level,
     * counts its rooms as reachable, and reports a NEGATIVE orphan count -- which is what the
     * first version of this class did. A diagnostic that can produce a nonsense number is worse
     * than no diagnostic, because people believe it.
     */
    private static Set<Room> reachableWithin(DungeonLevel level) {
        Set<Room> onThisLevel = new HashSet<>(level.getRooms());
        Set<Room> seen = new HashSet<>();
        Deque<Room> queue = new ArrayDeque<>();
        Room entrance = level.getRooms().get(0);
        queue.add(entrance);
        seen.add(entrance);
        while (!queue.isEmpty()) {
            for (Room next : queue.poll().getExits().values()) {
                if (onThisLevel.contains(next) && seen.add(next)) queue.add(next);
            }
        }
        return seen;
    }
}
