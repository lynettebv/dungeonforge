package dungeonforge.commands;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.Quest;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.Item;
import dungeonforge.states.GameOverState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 7 -- COMMAND (the INVOKER).
 *
 * A registry Map, not an if/else chain. Compare with the Week 6 gameLoop():
 *
 *   adding a verb   -> one register() line, instead of another else-if
 *   adding an alias -> one alias() line,    instead of "|| verb.equals(...)" everywhere
 *   undo            -> possible at all, because parse() returns an OBJECT
 *
 * The invoker never learns what any command does. It maps text to objects and hands them
 * over; the game loop calls execute(). That ignorance is the pattern working.
 */
public class CommandParser {

    private final Map<String, CommandFactory> registry = new LinkedHashMap<>();
    private final Map<String, String> aliases = new LinkedHashMap<>();

    private final QuestTracker quests;
    private final AchievementSystem achievements;

    public CommandParser(QuestTracker quests, AchievementSystem achievements) {
        this.quests = quests;
        this.achievements = achievements;
        registerDefaults();
    }

    public void register(String verb, CommandFactory factory) { registry.put(verb, factory); }
    public void alias(String shortForm, String verb)          { aliases.put(shortForm, verb); }

    public List<String> verbs() { return new ArrayList<>(registry.keySet()); }

    /** Text in, Command out. NEVER null -- see NoCommand. */
    public Command parse(String input, GameContext ctx) {
        if (input == null || input.isBlank()) return new NoCommand(ctx.getBus(), "");

        String trimmed = input.trim();
        int space = trimmed.indexOf(' ');
        String verb = (space < 0 ? trimmed : trimmed.substring(0, space)).toLowerCase();
        String args = space < 0 ? "" : trimmed.substring(space + 1).trim();

        verb = aliases.getOrDefault(verb, verb);

        CommandFactory factory = registry.get(verb);
        if (factory == null) {
            return new NoCommand(ctx.getBus(),
                    "You cannot '" + verb + "' here. Type 'help' for what you can do.");
        }
        if (!ctx.allowedVerbs().contains(verb)) {
            return new NoCommand(ctx.getBus(),
                    "You cannot do that in " + ctx.getState().name() + " mode. " +
                    "\n    Available here: " + String.join(", ", ctx.allowedVerbs()));
        }
        return factory.create(ctx, args);
    }

    private void registerDefaults() {
        // --- movement: three verbs, one class ---
        register("north", (c, a) -> new MoveCommand(c, "north"));
        register("south", (c, a) -> new MoveCommand(c, "south"));
        register("down",  (c, a) -> new MoveCommand(c, "down"));

        // WEEK 10 (PROVIDED) -- two more compass directions, so a level shape can be something
        // other than a line or a loop. Exits live in a Map keyed by direction name, so the set
        // of legal direction words is also the limit on how many doors a room can have.
        //
        // NOTE, and it is the exact collision Week 7's evidence sheet predicted: "e" is already
        // taken by `equip`, so east gets no short form. In an if/else chain that collision would
        // have compiled silently and the earlier branch would have won. Here both claims on "e"
        // would sit in one Map you can print, and the second would visibly overwrite the first.
        register("east",  (c, a) -> new MoveCommand(c, "east"));
        register("west",  (c, a) -> new MoveCommand(c, "west"));
        alias("w", "west");

        // WEEK 9 (PROVIDED) -- the whole wiring cost of two new verbs. Four lines.
        register("equip", EquipCommand::new);
        alias("e", "equip");
        register("enchant", EnchantCommand::new);
        alias("n", "north");
        alias("s", "south");
        alias("d", "down");
        alias("descend", "down");

        register("attack", AttackCommand::new);
        alias("a", "attack");
        alias("kill", "attack");
        alias("hit", "attack");

        register("take", TakeCommand::new);
        alias("get", "take");
        alias("t", "take");

        register("drop", DropCommand::new);

        register("use", UseItemCommand::new);
        alias("quaff", "use");
        alias("drink", "use");

        // --- MACRO: one word, many commands ---
        register("loot", (c, a) -> makeLootMacro(c));

        register("undo", (c, a) -> new SimpleCommand("undo", () -> {
            if (c.getHistory().undoLast()) c.getBus().message("You take that back.");
            else c.getBus().message("There is nothing left to undo.");
        }));
        alias("z", "undo");

        // --- read-only commands ---
        register("look", (c, a) -> new SimpleCommand("look",
                () -> c.getBus().message(describeRoom(c))));
        alias("l", "look");

        register("inventory", (c, a) -> new SimpleCommand("inventory", () -> {
            if (c.getPlayer().getInventory().isEmpty()) {
                c.getBus().message("You are carrying nothing.");
                return;
            }
            StringBuilder b = new StringBuilder("You are carrying:");
            for (Item i : c.getPlayer().getInventory()) b.append("\n    - ").append(i.describe());
            c.getBus().message(b.toString());
        }));
        alias("i", "inventory");
        alias("inv", "inventory");

        register("status", (c, a) -> new SimpleCommand("status",
                () -> c.getBus().message(c.getPlayer().describe())));

        register("quests", (c, a) -> new SimpleCommand("quests", () -> {
            StringBuilder b = new StringBuilder("Quests:");
            for (Quest q : quests.getQuests()) b.append("\n  ").append(q);
            b.append("\nAchievements: ").append(achievements.getUnlocked().isEmpty()
                    ? "(none yet)" : String.join(", ", achievements.getUnlocked()));
            c.getBus().message(b.toString());
        }));
        alias("q", "quests");

        register("history", (c, a) -> new SimpleCommand("history", () -> {
            List<String> log = c.getHistory().replayLog();
            StringBuilder b = new StringBuilder("Your session so far (" + log.size() + " actions):");
            for (String line : log) b.append("\n  ").append(line);
            b.append("\n  [undo stack depth: ").append(c.getHistory().depth()).append("]");
            c.getBus().message(b.toString());
        }));

        register("help", (c, a) -> new SimpleCommand("help",
                () -> c.getBus().message("[" + c.getState().name() + "] you can: "
                        + String.join(", ", c.allowedVerbs()))));
        alias("?", "help");

        register("pack", (c, a) -> new SimpleCommand("pack", c::openPack));
        register("back", (c, a) -> new SimpleCommand("back", c::closePack));


        register("quit", (c, a) -> new SimpleCommand("quit", () -> {
            c.getBus().message("You lay down your pack.");
            c.transitionTo(new GameOverState());
            c.stop();
        }));
        alias("exit", "quit");
    }

    /** WEEK 7 -- assembles a MACRO COMMAND at parse time from whatever is in the room. */
    private static Command makeLootMacro(GameContext ctx) {
        Room room = ctx.getCurrentRoom();
        List<Item> targets = new ArrayList<>(room.getFloorItems());
        if (room.hasChest()) targets.addAll(room.getChest().getContents());

        if (targets.isEmpty()) {
            return new NoCommand(ctx.getBus(), "There is nothing here to loot.");
        }
        List<Command> children = new ArrayList<>();
        for (Item item : targets) {
            children.add(new TakeCommand(ctx, item.getName()));
        }
        return new MacroCommand("loot x" + children.size(), children);
    }

    private static String describeRoom(GameContext ctx) {
        Room room = ctx.getCurrentRoom();
        DungeonLevel level = ctx.getWorld().levelContaining(room);
        StringBuilder b = new StringBuilder();
        b.append("[").append(room.getId()).append("] Level ").append(level.getDepth())
         .append(": ").append(level.getThemeName());
        if (!room.getFlavor().isEmpty()) b.append("\n  \"").append(room.getFlavor()).append("\"");
        if (room.hasLivingMonsters()) {
            b.append("\n  Hostile:");
            for (Monster m : room.getMonsters()) {
                if (m.isAlive()) b.append(" ").append(m.describe());
            }
        }
        for (Item i : room.getFloorItems()) b.append("\n  On the floor: ").append(i.describe());
        if (room.hasChest()) {
            b.append("\n  ").append(room.getChest().getName()).append(":");
            for (Item i : room.getChest().getContents()) b.append("\n    - ").append(i.describe());
        }
        b.append("\n  Exits: ").append(String.join(", ", room.getExits().keySet()));
        return b.toString();
    }
}
