package dungeonforge.commands;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.Quest;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public void alias(String shortForm, String verb) { aliases.put(shortForm, verb); }
    public List<String> verbs() {return new ArrayList<>(registry.keySet()); }

    public Command parse(String input, GameContext ctx) {
        if (input == null || input.isBlank()) return new NoCommand(ctx.getBus(), "");

        String trimmed = input.trim();
        int space = trimmed.indexOf(' ');
        String verb = (space < 0) ? trimmed : trimmed.substring(0, space).toLowerCase();
        String args = (space < 0) ? "" : trimmed.substring(space + 1).trim();

        verb = aliases.getOrDefault(verb, verb);

        CommandFactory factory = registry.get(verb);
        if (factory == null) {
            return new NoCommand(ctx.getBus(),
                    "You cannot '" + verb + "' here. Type 'help' for what you can do.");
        }
        return factory.create(ctx, args);
    }

    private void registerDefaults() {
        register("north", (GameContext c, String a) -> new MoveCommand(c, "north"));
        register("south", (GameContext c, String a) -> new MoveCommand(c, "south"));
        register("down", (GameContext c, String a) -> new MoveCommand(c, "down"));
        alias("n", "north");
        alias("s", "south");
        alias("d", "down");

        register("attack", AttackCommand::new);
        alias("a", "attack");
        alias("kill", "attack");
        alias("hit", "attack");

        register("take", TakeCommand::new);
        alias("get", "take");
        alias("t", "take");

        register("drop", DropCommand::new);

        register("use", UseItemCommand::new);
        alias("drink", "use");
        alias("quaff", "use");

        register("look", (GameContext c, String a) -> new SimpleCommand("look",
                () -> c.getBus().message(describeRoom(c))));

        register("inventory", (GameContext c, String a) -> new SimpleCommand("inventory",
                () -> {
            if (c.getPlayer().getInventory().isEmpty()) {
                c.getBus().message("You are carrying nothing.");
                return;
            }
            StringBuilder b = new StringBuilder("You are carrying:");
            for(Item i : c.getPlayer().getInventory()) b.append("\n      - ").append(i.describe());
            c.getBus().message(b.toString());
        }));
        alias("i", "inventory");

        register("status", (GameContext c, String a) -> new SimpleCommand("status",
                () -> c.getBus().message(c.getPlayer().describe())));

        register("quests", (GameContext c, String a) -> new SimpleCommand("quests", () -> {
            StringBuilder b = new StringBuilder("Quests:");
            for(Quest q : quests.getQuests()) b.append("\n      - ").append(q);
            b.append("\nAchievements: ").append(
                    achievements.getUnlocked().isEmpty() ? "(none yet)" :
                            String.join(", ", achievements.getUnlocked()) );
            c.getBus().message(b.toString());
        }));

        register("help", (GameContext c, String a) -> new SimpleCommand("help", () -> {
            c.getBus().message("Available: " + verbs() + "\n Aliases: " + aliases);
        }));
        alias("?", "help");

        register("quit", (GameContext c, String a) -> new SimpleCommand("quit", () -> {
            c.getBus().message("You lay down your pack.");
            c.stop();
        }));
        alias("exit", "quit");
    }

    public String describeRoom(GameContext ctx) {
        Room room = ctx.getCurrentRoom();
        DungeonLevel level = ctx.getWorld().levelContaining(room);
        StringBuilder b = new StringBuilder();
        b.append("[").append(room.getId()).append("Level ").append(level.getDepth()).append(": ")
                .append(level.getThemeName());
        if (!room.getFlavor().isEmpty()) b.append("\n   \"").append(room.getFlavor()).append("\"");
        if (room.hasLivingMonsters()) {
            b.append("\n Hostiles:");
            for (Monster m : room.getMonsters()) {
                if (m.isAlive()) b.append(" ").append(m.describe());
            }
        }
        for (Item i : room.getFloorItems()) b.append("\n On the floor: ").append(i.describe());
        if (room.getChest() != null && !room.getChest().getContents().isEmpty()) {
            b.append("\n    ").append(room.getChest().getName()).append(": ");
            for (Item i : room.getChest().getContents()) {
                b.append("\n    - ").append(i.describe());
            }
        }
        b.append("\n    Exits: ").append(String.join(", ", room.getExits().keySet()));
        return b.toString();
    }
}
