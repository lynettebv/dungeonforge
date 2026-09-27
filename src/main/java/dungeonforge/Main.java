package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * WEEK 3 -- the same demo, now reproducible.
 * The --seed flag works because there is exactly one RandomSource to reseed. With three
 * scattered Random objects it could not have been written at all.
 */
public final class Main {

    public static final String VERSION = "0.5.0";

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
        System.out.println("  seed " + RandomSource.getInstance().getSeed()
                + "  |  depth " + GameConfig.getInstance().getInt("dungeonDepth"));
        System.out.println();

        ArgumentParser parser = ArgumentParsers.newFor("Main").build()
                .defaultHelp(true)
                .description("Configure game player details and world seed value.");

        // Positional argument: Player name (String)
        parser.addArgument("-n", "--playerName")
                .dest("playerName")
                .type(String.class)
                .setDefault("Delver")
                .help("The name of the player");

        // Optional argument: seed (long)
        parser.addArgument("-s", "--seed")
                .dest("seed")
                .type(Long.class)
                .setDefault(-1L)
                .help("The name of the player");

        try {
            // Parse the arguments
            Namespace res = parser.parseArgs(args);
            // Extract the argument values

            Long seed = res.getLong("seed");
            if (seed >= 0) {
                RandomSource.getInstance().reseed(seed);
            }
            Player player = new Player(res.getString("playerName"));
            GameWorld world = new GameWorld(player);

            System.out.println(player.describe());
            System.out.println();

            for (DungeonLevel level : world.getLevels()) {
                System.out.println("-- Level " + level.getDepth() + ": " + level.getThemeName() + " --");
                for (Room room : level.getRooms()) {
                    StringBuilder line = new StringBuilder("  " + room.getId() + ": ");
                    if (room.getMonsters().isEmpty()) {
                        line.append("(empty)");
                    } else {
                        for (Monster m : room.getMonsters()) line.append(m.describe()).append("  ");
                    }
                    if (room.hasChest()) {
                        line.append(" [").append(room.getChest().getName()).append(": ");
                        for (var item : room.getChest().getContents()) line.append(item.getName()).append(", ");
                        line.setLength(line.length() - 2);
                        line.append("]");
                    }
                    System.out.println(line.toString().trim());
                    if (!room.getFlavor().isEmpty() && room.getMonsters().isEmpty() && !room.hasChest()) {
                        System.out.println("        \"" + room.getFlavor() + "\"");
                    }
                }
            }
            System.out.println();
            System.out.println("=== THE DELVE ===");
            delve(world, player);

            System.out.println();
            System.out.println("Themes registered: " + world.getThemes().themeNames());
            System.out.println("Monster blueprints loaded: " + world.getMonsterFactory().blueprintCount());
            System.out.println("Total monsters: " + world.totalMonsters() + "   Total loot: " + world.totalLoot());
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    /** Walks the whole dungeon, fighting whatever is in the way. */
    private static void delve (GameWorld world, Player player){
        Combat combat = new Combat();
        for (DungeonLevel level : world.getLevels()) {
            System.out.println("  Descending to level " + level.getDepth()
                    + " (" + level.getThemeName() + ")");
            for (Room room : level.getRooms()) {
                if (!room.getMonsters().isEmpty()) {
                    System.out.println("    " + room.getId());
                    if (!combat.fight(player, room, level.getDepth())) return;   // died
                }
                Combat.restAfterRoom(player);
            }
        }
        System.out.println();
        System.out.println(player.isAlive()
                ? "  You climb back into daylight. " + player.describe()
                : "  You die in the dark. XP " + player.getXp() + ", gold " + player.getGold());
    }
}
