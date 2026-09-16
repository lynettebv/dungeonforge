package dungeonforge;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 2 -- the walking skeleton, now running a small demo of the Week 1 domain.
 *
 * WEEK 3 EXERCISE, and do this FIRST, before you write any code:
 *
 *     mvn -q exec:java > run1.txt
 *     mvn -q exec:java > run2.txt
 *     diff run1.txt run2.txt
 *
 * The two runs differ, and there is no way to ask for the dungeon you saw the first time.
 * That is the concrete problem this week's pattern solves. Save the diff -- your Definition
 * of Done asks for evidence.
 */
public final class Main {

    public static final String VERSION = "0.2.0";

    private Main() { }

    public static String banner() {
        return """
                =========================================
                        D U N G E O N F O R G E
                  A Head First Design Patterns project
                =========================================""";
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("  version " + VERSION);
        System.out.println();

        Player player = new Player(args.length > 0 ? args[0] : "Delver");
        GameWorld world = new GameWorld(player);

        System.out.println(player.describe());
        System.out.println();

        for (DungeonLevel level : world.getLevels()) {
            System.out.println("-- Level " + level.getDepth() + " --");
            for (Room room : level.getRooms()) {
                StringBuilder line = new StringBuilder("  " + room.getId() + ": ");
                if (room.getMonsters().isEmpty()) {
                    line.append("(empty)");
                } else {
                    for (Monster m : room.getMonsters()) line.append(m.describe()).append("  ");
                }
                System.out.println(line.toString().trim());
            }
        }
        System.out.println();
        System.out.println("Total monsters: " + world.totalMonsters());
    }
}
