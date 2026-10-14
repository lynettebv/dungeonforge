package dungeonforge;

import dungeonforge.commands.Command;
import dungeonforge.commands.CommandHistory;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;
import dungeonforge.core.Combat;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.config.RandomSource;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.CombatLog;
import dungeonforge.events.DangerMeter;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.events.GameEventListener;
import dungeonforge.events.Quest;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * WEEK 7 -- the game is now INTERACTIVE. You type; things happen.
 *
 * ======================= THE PROBLEM, ALL IN ONE METHOD =======================
 * Look at gameLoop() below. It is an if/else chain and it is already unpleasant at eleven
 * verbs. Ask yourself, honestly, before you read the stories:
 *
 *   1. Where would you add "equip"?  And "save"?  This method grows forever.
 *   2. How would you add an ALIAS, so that "n" means "north"? Another condition on every
 *      branch that needs one.
 *   3. How would you add UNDO? Stop and actually think about this one. There is nowhere to
 *      put it, because nothing in this design REMEMBERS what just happened. The action was
 *      an if-branch, and an if-branch is not a thing you can keep.
 *   4. How would you record a session and replay it for a bug report? Same answer: there is
 *      nothing to record.
 *
 * Question 3 is the one that matters. Everything else is inconvenience; that one is
 * impossible. Write your answers in docs/evidence.md before you start.
 * =============================================================================
 */
public final class Main {

    public static final String VERSION = "0.6.0";

    private Main() { }

    public static String banner() {
        return """
                =========================================
                        D U N G E O N F O R G E
                  A Head First Design Patterns project
                =========================================""";
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("dungeonforge").build()
                .defaultHelp(true)
                .description("A turn-based dungeon crawler built one design pattern at a time.");

        parser.addArgument("-n", "--playerName")
                .dest("playerName").type(String.class).setDefault("Delver")
                .help("The name of the player");
        parser.addArgument("-s", "--seed")
                .dest("seed").type(Long.class).setDefault(-1L)
                .help("World seed; omit to use the value in config.json");

        try {
            Namespace res = parser.parseArgs(args);

            Long seed = res.getLong("seed");
            if (seed != null && seed >= 0) RandomSource.getInstance().reseed(seed);

            System.out.println(banner());
            System.out.println("  version " + VERSION + "   seed " + RandomSource.getInstance().getSeed());
            System.out.println();

            Player player = new Player(res.getString("playerName"));
            player.addItem(new Potion("Small Healing Draught", 0.3, 20, 22));
            GameWorld world = new GameWorld(player);

            EventBus bus = new EventBus();
            QuestTracker quests = new QuestTracker(bus);
            AchievementSystem achievements = new AchievementSystem(bus);
            CombatLog log = new CombatLog(200);
            DangerMeter meter = new DangerMeter(bus, player, 0.25);
            bus.subscribe(quests);
            bus.subscribe(achievements);
            bus.subscribe(log);
            bus.subscribe(meter);
            bus.subscribe(new ConsolePrinter());

            CommandHistory history = new CommandHistory();
            GameContext ctx = new GameContext(world, player, bus, new Combat(bus), history);
            CommandParser cmds = new CommandParser(quests, achievements);

            gameLoop(cmds, ctx);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }

    /**
     * TODO(week 7): eleven verbs, one method, and no way to take anything back.
     */
    private static void gameLoop(CommandParser parser, GameContext ctx) {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        while (ctx.isRunning() && ctx.getPlayer().isAlive()) {
            System.out.print("\n\n[HP " + ctx.getPlayer().getHp() + "/" + ctx.getPlayer().getMaxHp() + "] > ");
            System.out.flush();

            String line;
            try { line = in.readLine(); } catch (Exception e) { break; }
            if (line == null) break;
            System.out.println();

            Command command = parser.parse(line,ctx);
            command.execute();
        }
    }

    private static String describe(GameWorld world, Room room) {
        DungeonLevel level = world.levelContaining(room);
        StringBuilder b = new StringBuilder();
        b.append("[").append(room.getId()).append("] Level ").append(level.getDepth())
         .append(": ").append(level.getThemeName());
        if (!room.getFlavor().isEmpty()) b.append("\n  \"").append(room.getFlavor()).append("\"");
        if (room.hasLivingMonsters()) {
            b.append("\n  Hostile:");
            for (Monster m : room.getMonsters()) if (m.isAlive()) b.append(" ").append(m.describe());
        }
        for (Item i : room.getFloorItems()) b.append("\n  On the floor: ").append(i.describe());
        if (room.getChest() != null && !room.getChest().getContents().isEmpty()) {
            b.append("\n  ").append(room.getChest().getName()).append(":");
            for (Item i : room.getChest().getContents()) b.append("\n    - ").append(i.describe());
        }
        b.append("\n  Exits: ").append(String.join(", ", room.getExits().keySet()));
        return b.toString();
    }

    /** The console view. It prints; nothing else does. */
    private static final class ConsolePrinter implements GameEventListener {
        private final CombatLog formatter = new CombatLog(1);

        @Override
        public void onEvent(GameEvent event) {
            if (event.getType() == EventType.ROOM_CLEARED) return;
            formatter.onEvent(event);
            var lines = new ArrayList<>(formatter.getLines());
            if (!lines.isEmpty()) {
                System.out.println("  " + lines.get(lines.size() - 1));
                formatter.clear();
            }
        }
    }
}
