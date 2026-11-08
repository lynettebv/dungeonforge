package dungeonforge;

import dungeonforge.commands.Command;
import dungeonforge.commands.CommandHistory;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.CombatLog;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.events.GameEventListener;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Potion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * WEEK 7 -- the game loop, after COMMAND.
 *
 * Compare gameLoop() below with the Week 6 version. It went from about seventy lines of
 * if/else to four lines, and it no longer knows what any verb means:
 *
 *     Command c = parser.parse(line, ctx);
 *     c.execute();
 *     history.push(c);
 *
 * The loop is now a thing that could drive ANY command set. --script proves it: the same
 * three lines replay a recorded session with no interactive input at all.
 */
public final class Main {

    public static final String VERSION = "0.9.0";

    private Main() { }

    public static String banner() {
        return """
                =========================================
                        D U N G E O N F O R G E
                  A Head First Design Patterns project
                =========================================""";
    }

    public static void main(String[] args) throws Exception {
        String name = "Delver";
        String script = null;
        for (String a : args) {
            if (a.startsWith("--seed="))        RandomSource.getInstance().reseed(Long.parseLong(a.substring(7).trim()));
            else if (a.startsWith("--script=")) script = a.substring(9).trim();
            else if (!a.startsWith("--"))       name = a;
        }

        System.out.println(banner());
        System.out.println("  version " + VERSION + "   seed " + RandomSource.getInstance().getSeed());
        System.out.println();

        Player player = new Player(name);
        player.addItem(new Potion("Small Healing Draught", 0.3, 20, 22));
        player.addItem(new Hourglass(2));                 // US-5.4
        // WEEK 9 (PROVIDED) -- a plain weapon to start with, so the enchantment exercises in
        // docs/evidence.md work on turn one instead of waiting on a loot drop.
        player.addItem(new dungeonforge.items.Weapon("Notched Shortsword", 2.5, 25, 5));
        GameWorld world = new GameWorld(player);

        EventBus bus = new EventBus();
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        CombatLog log = new CombatLog(200);
        bus.subscribe(quests);
        bus.subscribe(achievements);
        bus.subscribe(log);
        bus.subscribe(new ConsolePrinter());

        CommandHistory history = new CommandHistory();
        GameContext ctx = new GameContext(world, player, bus, new Combat(bus), history);
        CommandParser parser = new CommandParser(quests, achievements);

        bus.message("Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.");
        parser.parse("look", ctx).execute();
        ctx.syncState();

        if (script != null) {
            runScript(parser, ctx, history, Files.readAllLines(Path.of(script)));
        } else {
            gameLoop(parser, ctx, history);
        }

        System.out.println();
        System.out.println("  " + player.describe());
        if (!player.isAlive()) System.out.println("  You die in the dark.");
        System.out.println("  Actions this session: " + history.replayLog().size()
                + "   |   undo stack depth: " + history.depth());
    }

    /** THE ENTIRE GAME LOOP. Four lines of work. */
    private static void gameLoop(CommandParser parser, GameContext ctx, CommandHistory history) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        ctx.syncState();
        while (ctx.isRunning() && ctx.getPlayer().isAlive()) {
            System.out.print("\n["+ctx.getState().name()+" | HP " + ctx.getPlayer().getHp() + "/"
                    + ctx.getPlayer().getMaxHp() + "] > ");
            System.out.flush();

            String line;
            try { line = in.readLine(); } catch (Exception e) { break; }
            if (line == null) break;
            System.out.println();

            Command command = parser.parse(line, ctx);   // text  -> object
            command.execute();                            // object -> effect
            history.push(command);                        // object -> undo stack + replay log
            ctx.syncState();
        }
    }

    /**
     * WEEK 7 -- the capability nobody asked for.
     *
     * Because actions are objects, a recorded session is just a list of strings, and replaying
     * it uses the SAME three lines as interactive play. Attach one of these to a bug report
     * and the bug reproduces exactly -- which only works because Week 3 made the world seeded.
     */
    private static void runScript(CommandParser parser, GameContext ctx,
                                  CommandHistory history, List<String> lines) {
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("#")) continue;
            if (!ctx.isRunning() || !ctx.getPlayer().isAlive()) break;
            System.out.println("\n[script] > " + line);
            Command command = parser.parse(line, ctx);
            command.execute();
            history.push(command);
        }
    }

    /** The console view. It prints; nothing else does. */
    private static final class ConsolePrinter implements GameEventListener {
        private final CombatLog formatter = new CombatLog(1);

        @Override
        public void onEvent(GameEvent event) {
            if (event.getType() == EventType.ROOM_CLEARED) return;
            formatter.onEvent(event);
            var lines = formatter.getLines();
            if (!lines.isEmpty()) {
                System.out.println("  " + lines.get(lines.size() - 1));
                formatter.clear();          // otherwise an event with no text re-prints the last one
            }
        }
    }
}
