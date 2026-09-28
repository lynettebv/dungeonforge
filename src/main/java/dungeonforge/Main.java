package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Iterator;

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
            int monstersAtStart = world.totalMonsters();
            int lootAtStart = world.totalLoot();

            System.out.println("=== THE DELVE ===");

            // WEEK 5 -- Observer wiring. This is the ONLY place the listeners are named.
            // Combat has no idea any of them exist.
            EventBus bus = new EventBus();
            QuestTracker quests = new QuestTracker(bus);
            AchievementSystem achievements = new AchievementSystem(bus);
            CombatLog log = new CombatLog(200);
            bus.subscribe(quests);
            bus.subscribe(achievements);
            bus.subscribe(log);

            delve(world, player, bus);

            var lines = log.getLines();

            System.out.println();
            System.out.println("-- combat log: opening --");
            Iterator<String> it = lines.iterator();
            int i = 0;
            while(i < 8 && it.hasNext()) {
                System.out.println("  " + it.next());
                i++;
            }

            // The lines that prove the STRATEGY pattern is doing something. Each of these is a
            // monster behaving differently from another monster, or from its own earlier self.
            System.out.println();
            System.out.println("-- strategy highlights --");
            int shown = 0;
            for (String line : lines) {
                if (line.contains("changes tactics") || line.contains("flees")
                || line.contains("mends") || line.contains("circles")) {
                    System.out.println("  " + line);
                    if(++shown >= 10) break;
                }
            }
            if (shown == 0) System.out.println("   (none this seed -- try --seed=3)");

            System.out.println();
            System.out.println("-- combat log: ending --");
            lines.stream()
                    .skip(Math.max(0, lines.size() - 6))
                    .forEach((line) -> {
                        System.out.println("  " + line);
                    });

            System.out.println();
            System.out.println("-- quests --");
            for (Quest q : quests.getQuests()) System.out.println("  " + q);

            System.out.println();
            System.out.println("-- achievements --");
            if (achievements.getUnlocked().isEmpty()) System.out.println("  (none)");
            for (String a : achievements.getUnlocked()) System.out.println("  " + a);

            System.out.println();
            System.out.println("Listeners on the bus: " + bus.listenerCount()
                    + "  |  log lines captured:  " + log.size());

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
    private static void delve (GameWorld world, Player player, EventBus bus){
        Combat combat = new Combat(bus);
        for (DungeonLevel level : world.getLevels()) {
            bus.publish(GameEvent.of(EventType.LEVEL_ENTERED,
                    "depth", level.getDepth(), "theme", level.getThemeName()));
            for (Room room : level.getRooms()) {
                if (!combat.fight(player, room, level.getDepth())) {
                    System.out.println("  " + player.describe());
                    return;
                }
                Combat.restAfterRoom(player);
            }
        }
        bus.publish(GameEvent.of(EventType.DELVE_SURVIVED));
        System.out.println(" " + player.describe());
    }
}
