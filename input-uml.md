

File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\Action.java
```java
package dungeonforge.behavior;

import dungeonforge.core.Entity;

/**
 * WEEK 5 -- what a STRATEGY returns.
 *
 * Note that a strategy returns a DECISION, it does not perform one. Deciding and doing are
 * separate jobs: the strategy knows what this monster wants to do, and Combat knows how to
 * make it happen. Keeping those apart is what lets you unit-test a strategy without a
 * running game.
 */
public class Action {

    public enum Type { ATTACK, RANGED_ATTACK, FLEE, HEAL_ALLY, WAIT }

    private final Type type;
    private final Entity target;
    private final String flavor;

    public Action(Type type, Entity target, String flavor) {
        this.type = type;
        this.target = target;
        this.flavor = flavor;
    }

    public static Action wait(String flavor) { return new Action(Type.WAIT, null, flavor); }

    public Type getType()     { return type; }
    public Entity getTarget() { return target; }
    public String getFlavor() { return flavor; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\AggressiveStrategy.java
```java
package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/** WEEK 5 -- STRATEGY. Swings every turn, whatever the odds. */
public class AggressiveStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        return new Action(Action.Type.ATTACK, target, self.getName() + " lunges at you");
    }

    @Override public String name() { return "aggressive"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\CombatStrategy.java
```java
package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY.
 *
 * "Define a family of algorithms, encapsulate each one, and make them interchangeable.
 *  Strategy lets the algorithm vary independently from clients that use it."
 *
 * ===================== WHY NOT JUST SUBCLASS MONSTER? =====================
 * Because behaviour varies INDEPENDENTLY of species. Any species can be aggressive; any
 * species can be skittish; and the same skeleton needs to switch from one to the other
 * halfway through a fight.
 *
 * Subclassing gives you AggressiveSkeleton, SkittishSkeleton, RangedSkeleton, AggressiveImp,
 * SkittishImp... one class per combination. 15 species x 4 behaviours = 60 classes, and a
 * monster still could not change its mind mid-fight, because you cannot change an object's
 * class at runtime.
 *
 * Composition gives you 15 species + 4 strategies = 19 classes, and a one-line swap.
 * That is the SimUDuck argument, and Part D1 asks you to do the arithmetic yourself.
 * =========================================================================
 *
 * Compare with Week 8's State pattern, which has nearly identical UML and the opposite
 * intent. Strategy answers HOW to fight; State will answer WHEN to fight, flee or rage.
 */
public interface CombatStrategy {

    /** Decide, do not act. Combat performs whatever this returns. */
    Action chooseAction(Monster self, Player target, Room room);

    /** For logs and tests. */
    String name();
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\HealerStrategy.java
```java
package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY.
 *
 * Worth noticing: this strategy reads the ROOM, not just itself. A strategy may look at
 * anything it is handed. What it must not do is mutate the world -- it returns a decision and
 * Combat carries it out.
 */
public class HealerStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        Monster worst = null;
        for (Monster m : room.getMonsters()) {
            if (m == self || !m.isAlive()) continue;
            if (worst == null || m.hpFraction() < worst.hpFraction()) worst = m;
        }
        if (worst != null && worst.hpFraction() < 0.7) {
            return new Action(Action.Type.HEAL_ALLY, worst, self.getName() + " chants over " + worst.getName());
        }
        return new Action(Action.Type.ATTACK, target, self.getName() + " strikes with a censer");
    }

    @Override public String name() { return "healer"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\RangedStrategy.java
```java
package dungeonforge.behavior;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/** WEEK 5 -- STRATEGY. Keeps its distance: weaker hits, and sometimes it just repositions. */
public class RangedStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        if (RandomSource.getInstance().nextDouble() < 0.25) {
            return Action.wait(self.getName() + " circles, looking for an angle");
        }
        return new Action(Action.Type.RANGED_ATTACK, target, self.getName() + " looses a shot");
    }

    @Override public String name() { return "ranged"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\behavior\SkittishStrategy.java
```java
package dungeonforge.behavior;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY, and the one that makes the pattern click.
 *
 * Combat SWAPS a badly wounded monster to this strategy mid-fight. The monster object does
 * not change class, does not get rebuilt, and keeps its hit points and its name -- it simply
 * starts making different decisions. That is only possible because behaviour is an object it
 * HAS rather than something it IS.
 */
public class SkittishStrategy implements CombatStrategy {

    @Override
    public Action chooseAction(Monster self, Player target, Room room) {
        double threshold = GameConfig.getInstance().getDouble("fleeThreshold");
        if (self.hpFraction() < threshold) {
            return new Action(Action.Type.FLEE, null, self.getName() + " scrabbles backwards");
        }
        return new Action(Action.Type.ATTACK, target, self.getName() + " snaps at you nervously");
    }

    @Override public String name() { return "skittish"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\AbstractCommand.java
```java
package dungeonforge.commands;

import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.EventBus;

/**
 * WEEK 7 -- shared plumbing for COMMAND.
 *
 * Note the shape of execute(): it is FINAL and it defines the order of a turn --
 * snapshot, act, and if the action really happened, let the monsters respond.
 * Subclasses fill in doExecute() only.
 *
 * If that feels familiar, it should: you are looking at a Template Method three weeks before
 * Week 10 formally teaches it. Patterns do not arrive in the order a textbook lists them.
 */
public abstract class AbstractCommand implements Command {

    protected final GameContext ctx;
    protected final String args;

    private TurnSnapshot before;
    private boolean actuallyRan;

    protected AbstractCommand(GameContext ctx, String args) {
        this.ctx = ctx;
        this.args = args == null ? "" : args.trim();
    }

    protected Player player()  { return ctx.getPlayer(); }
    protected Room room()      { return ctx.getCurrentRoom(); }
    protected EventBus bus()   { return ctx.getBus(); }

    @Override
    public final void execute() {
        if (endsTurn()) {
            before = TurnSnapshot.capture(ctx.getPlayer(), ctx.getCurrentRoom());
        }

        actuallyRan = doExecute();

        if (!actuallyRan) {
            before = null;              // a refused command does not go on the undo stack
            return;
        }
        if (endsTurn()) {
            ctx.runMonsterTurns();
        }
    }

    /**
     * Returns FALSE if the command could not run -- no such exit, nothing to attack.
     * A refused command must not burn a turn and must not be undoable, which is why this
     * returns a boolean instead of void.
     */
    protected abstract boolean doExecute();

    @Override
    public void undo() {
        if (before != null) {
            before.restore();
            ctx.setCurrentRoom(before.getRoom());
        }
    }

    /**
     * A command that was REFUSED must not go on the undo stack.
     *
     * "There is no way south" is not a move you should be able to take back -- and if it went
     * on the stack, the player's next undo would silently do nothing while consuming their
     * hourglass charge. This is the kind of bug that only shows up once undo exists, which is
     * why it is worth stating in code rather than in a comment.
     */
    @Override
    public boolean isUndoable() { return actuallyRan; }

    public boolean didRun() { return actuallyRan; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\AttackCommand.java
```java
package dungeonforge.commands;

import dungeonforge.core.Monster;

/**
 * WEEK 7 -- COMMAND, and the one that teaches the hard half of undo.
 *
 * Attacking ends your turn, so the monsters strike back before control returns. Undoing this
 * command therefore has to reverse THE WHOLE EXCHANGE -- their damage, your XP, your gold,
 * and any monster that turned skittish or fled. See TurnSnapshot.
 */
public class AttackCommand extends AbstractCommand {

    public AttackCommand(GameContext ctx, String targetName) { super(ctx, targetName); }

    @Override
    protected boolean doExecute() {
        Monster target = findTarget();
        if (target == null) {
            bus().message(args.isBlank()
                    ? "There is nothing here to attack."
                    : "There is nothing here called '" + args + "'.");
            return false;
        }
        ctx.getCombat().playerStrikes(player(), target);
        return true;
    }

    private Monster findTarget() {
        if (args.isBlank()) return room().firstLivingIn();
        for (Monster m : room().getMonsters()) {
            if (m.isAlive() && m.getName().toLowerCase().contains(args.toLowerCase())) return m;
        }
        return null;
    }

    @Override public String getDescription() { return ("attack " + args).trim(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\Command.java
```java
package dungeonforge.commands;

/**
 * WEEK 7 -- COMMAND.
 *
 * "Encapsulate a request as an object, thereby letting you parameterize clients with
 *  different requests, queue or log requests, and support undoable operations."
 *
 * That definition lists four capabilities. Notice that we asked for exactly ONE of them
 * (undo) and the other three arrive whether we wanted them or not:
 *
 *   parameterize  -- an alias map and a hotkey bar both become one line of wiring
 *   queue         -- a macro is a list of Commands
 *   log           -- a replayable session log is just the list of descriptions
 *   undoable      -- the one we actually needed
 *
 * The reason all four come together is that they are the same capability wearing different
 * hats: once an action is an OBJECT rather than an if-branch, you can store it, copy it,
 * name it, put it in a list, and ask it to reverse itself. An if-branch is not a thing you
 * can keep.
 */
public interface Command {

    /** Do the thing. */
    void execute();

    /** Put the world back. Only meaningful if isUndoable() is true. */
    void undo();

    /** What appears in the replay log, e.g. "attack Skeleton". */
    String getDescription();

    /** Look, help and inventory change nothing, so they never go on the undo stack. */
    default boolean isUndoable() { return true; }

    /** Turn-ending commands let the monsters act afterwards. */
    default boolean endsTurn() { return true; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\CommandFactory.java
```java
package dungeonforge.commands;

/**
 * WEEK 7 -- what lives in the parser's registry instead of an if-branch.
 *
 * A functional interface, so a registration is one lambda: the verb "north" maps to
 * (ctx, args) -> new MoveCommand(ctx, "north"). Week 4's Simple Factory idiom, at one line each.
 */
@FunctionalInterface
public interface CommandFactory {
    Command create(GameContext ctx, String args);
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\CommandHistory.java
```java
package dungeonforge.commands;

import dungeonforge.config.GameConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * WEEK 7 -- the undo stack, and the replay log, from the same list of objects.
 *
 * The replay log is the capability nobody asked for. Because every action is an object with a
 * description, "what did the player actually do?" is answerable -- which is exactly what you
 * want attached to a bug report. Part D3 has you record a session and replay it.
 */
public class CommandHistory {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final List<String> replayLog = new ArrayList<>();
    private final int maxDepth;

    public CommandHistory() {
        this.maxDepth = Math.max(1, GameConfig.getInstance().getInt("undoDepth"));
    }

    /** Every command that RAN gets logged; only undoable ones join the stack. */
    public void push(Command command) {
        replayLog.add(command.getDescription());
        if (!command.isUndoable()) return;
        undoStack.push(command);
        while (undoStack.size() > maxDepth) undoStack.removeLast();
    }

    public boolean undoLast() {
        if (undoStack.isEmpty()) return false;
        undoStack.pop().undo();
        return true;
    }

    public int depth()              { return undoStack.size(); }
    public List<String> replayLog() { return new ArrayList<>(replayLog); }
    public void clear()             { undoStack.clear(); replayLog.clear(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\CommandParser.java
```java
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
                    "\n Available here: " + String.join(", ", ctx.allowedVerbs()));
        }
        return factory.create(ctx, args);
    }

    private void registerDefaults() {
        // --- movement: three verbs, one class ---
        register("north", (c, a) -> new MoveCommand(c, "north"));
        register("south", (c, a) -> new MoveCommand(c, "south"));
        register("down",  (c, a) -> new MoveCommand(c, "down"));
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
        for (Monster m : room.getMonsters()) {
            if (m.isAlive()) {
                b.append("\n  Hostile: ").append(m.describe());
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

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\DropCommand.java
```java
package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

/**
 * WEEK 7 -- COMMAND.
 *
 * Added last, and it took about four minutes: one class and one line in the parser's
 * registry. Compare that with adding a verb to the Week 6 if/else chain, and with what it
 * would have cost to give that version an undo.
 */
public class DropCommand extends AbstractCommand {

    public DropCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    @Override
    protected boolean doExecute() {
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You are not carrying anything like that.");
            return false;
        }
        player().removeItem(item);
        room().getFloorItems().add(item);
        bus().publish(GameEvent.message("You drop " + item.getName() + "."));
        return true;
    }

    @Override public String getDescription() { return ("drop " + args).trim(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\GameContext.java
```java
package dungeonforge.commands;

import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.EventBus;
import dungeonforge.states.*;

import java.util.List;

/**
 * WEEK 7 -- the RECEIVER, bundled.
 *
 * In Head First's remote control, each Command holds its own receiver -- a Light, a Stereo.
 * Ours all act on the same handful of objects, so they share one context rather than each
 * storing four references.
 *
 * Week 8 grows this class into the State pattern's Context. It is deliberately small now.
 */
public class GameContext {

    private final GameWorld world;
    private final Player player;
    private final EventBus bus;
    private final Combat combat;
    private final CommandHistory history;

    private Room currentRoom;
    private boolean running = true;

    private GameState state = new ExploringState();

    public GameContext(GameWorld world, Player player, EventBus bus,
                       Combat combat, CommandHistory history) {
        this.world = world;
        this.player = player;
        this.bus = bus;
        this.combat = combat;
        this.history = history;
        this.currentRoom = world.startingRoom();
    }

    public void transitionTo(GameState next) {
        if (next == null || next.getClass() == state.getClass()) return;
        state.onExit(this);
        state = next;
        next.onEnter(this);
    }

    public void syncState() {
        if (!player.isAlive() || !running) {
            transitionTo(new GameOverState());
            return;
        }
        if (state instanceof InventoryState) return;

        if (currentRoom.hasLivingMonsters()) transitionTo(new CombatState());
        else transitionTo(new ExploringState());
    }

    public List<String> allowedVerbs() { return state.availableCommands(); }


    public GameWorld getWorld()          { return world; }
    public Player getPlayer()            { return player; }
    public EventBus getBus()             { return bus; }
    public Combat getCombat()            { return combat; }
    public CommandHistory getHistory()   { return history; }
    public Room getCurrentRoom()         { return currentRoom; }
    public void setCurrentRoom(Room r)   { this.currentRoom = r; }
    public boolean isRunning()           { return running; }
    public void stop()                   { this.running = false; }
    public GameState getState() { return state; }

    public void openPack() {transitionTo(new InventoryState());}
    public void closePack() {transitionTo(new ExploringState());}

    /** After a turn-ending command, the monsters get their round. */
    public void runMonsterTurns() {
        if (player.isAlive()) combat.monsterTurns(player, currentRoom);
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\MacroCommand.java
```java
package dungeonforge.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 7 -- MACRO COMMAND.
 *
 * "loot" is one word that becomes several TakeCommands. Note that this class holds
 * List<Command> and knows nothing about taking, moving or attacking -- it composes the
 * interface, not the implementations. Any command can go in a macro, including another macro.
 *
 * ==================== THE QUESTION TO SIT WITH ====================
 * undo() runs the children in REVERSE order. Work out why before you read on.
 *
 * Because each child captured its before-state at the moment IT ran, and those states are
 * nested like brackets. Undoing them forwards would restore an early snapshot and then have
 * a later snapshot overwrite it with the very state you were trying to escape.
 * ==================================================================
 */
public class MacroCommand implements Command {

    private final List<Command> children = new ArrayList<>();
    private final String description;

    public MacroCommand(String description, List<Command> children) {
        this.description = description;
        this.children.addAll(children);
    }

    @Override
    public void execute() {
        for (Command c : children) c.execute();
    }

    @Override
    public void undo() {
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).undo();
        }
    }

    @Override public String getDescription() { return description; }
    @Override public boolean endsTurn()      { return false; }   // children each ended their own

    public int size() { return children.size(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\MoveCommand.java
```java
package dungeonforge.commands;

import dungeonforge.core.Combat;
import dungeonforge.core.Room;
import dungeonforge.events.GameEvent;

/** WEEK 7 -- COMMAND. */
public class MoveCommand extends AbstractCommand {

    private final String direction;

    public MoveCommand(GameContext ctx, String direction) {
        super(ctx, direction);
        this.direction = direction.toLowerCase();
    }

    @Override
    protected boolean doExecute() {
        Room next = room().getExit(direction);
        if (next == null) {
            bus().message("There is no way " + direction + ".");
            return false;
        }
        ctx.setCurrentRoom(next);
        Combat.restAfterRoom(player());
        bus().publish(GameEvent.message("You go " + direction + "."));
        return true;
    }

    @Override public String getDescription() { return direction; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\NoCommand.java
```java
package dungeonforge.commands;

import dungeonforge.events.EventBus;

/**
 * WEEK 7 -- NULL OBJECT, a small bonus pattern Head First introduces alongside Command.
 *
 * The parser always returns a Command, never null. That means the game loop is three lines
 * with no null check, and "unknown verb" is handled by an object rather than by an if.
 *
 * Worth noticing: this is the same instinct as Week 5's decision that a strategy always
 * exists. Removing special cases is a recurring move, not a one-off trick.
 */
public class NoCommand implements Command {

    private final EventBus bus;
    private final String reason;

    public NoCommand(EventBus bus, String reason) {
        this.bus = bus;
        this.reason = reason;
    }

    @Override
    public void execute() {
        if (bus != null && reason != null && !reason.isBlank()) bus.message(reason);
    }

    @Override public void undo() { }
    @Override public String getDescription() { return "(nothing)"; }
    @Override public boolean isUndoable() { return false; }
    @Override public boolean endsTurn()   { return false; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\SimpleCommand.java
```java
package dungeonforge.commands;

/**
 * WEEK 7 -- a read-only COMMAND for look, help, inventory, quests.
 *
 * These change nothing, so they are never undoable and never end a turn. Keeping them as
 * Commands anyway means the parser has exactly one return type and the game loop has exactly
 * one shape. Special cases are what we are trying to delete.
 */
public class SimpleCommand implements Command {

    /** A no-argument, no-return action. Java's Runnable would also do; this reads better here. */
    public interface Body { void run(); }

    private final String description;
    private final Body body;

    public SimpleCommand(String description, Body body) {
        this.description = description;
        this.body = body;
    }

    @Override public void execute()          { body.run(); }
    @Override public void undo()             { }
    @Override public String getDescription() { return description; }
    @Override public boolean isUndoable()    { return false; }
    @Override public boolean endsTurn()      { return false; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\TakeCommand.java
```java
package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

/** WEEK 7 -- COMMAND. Picks an item off the floor or out of a chest. */
public class TakeCommand extends AbstractCommand {

    public TakeCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    @Override
    protected boolean doExecute() {
        Item found = takeFromFloor();
        if (found == null) found = takeFromChest();

        if (found == null) {
            bus().message("There is nothing like that here.");
            return false;
        }
        player().addItem(found);
        bus().publish(GameEvent.message("You take " + found.getName() + "."));
        return true;
    }

    private Item takeFromFloor() {
        for (Item i : room().getFloorItems()) {
            if (matches(i)) { room().getFloorItems().remove(i); return i; }
        }
        return null;
    }

    private Item takeFromChest() {
        if (!room().hasChest()) return null;
        for (Item i : room().getChest().getContents()) {
            if (matches(i)) { room().getChest().getContents().remove(i); return i; }
        }
        return null;
    }

    private boolean matches(Item i) {
        return args.isBlank() || i.getName().toLowerCase().contains(args.toLowerCase());
    }

    @Override public String getDescription() { return ("take " + args).trim(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\TurnSnapshot.java
```java
package dungeonforge.commands;

import dungeonforge.behavior.CombatStrategy;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.items.Item;
import dungeonforge.states.MonsterState;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 7 -- the BEFORE-state a turn-ending Command captures so undo() can work.
 *
 * ==================== READ THIS, IT IS THE POINT OF PART D2 ====================
 * The textbook version of undo is "remember the one field I changed." For
 * AttackCommand that would be "remember the monster's hit points."
 *
 * Try it and it does not work. Attacking ends your turn, so the monsters retaliate. Undoing
 * the attack must also undo the damage they did to you, the XP you gained, the gold, and any
 * monster that changed tactics or fled. One remembered field cannot do that.
 *
 * The honest fix is to snapshot everything a turn can touch. That is a small MEMENTO --
 * Head First Ch. 14's appendix -- and NOTICING WHY IT WAS NECESSARY is worth more than the
 * class itself. Command gives you the hook for undo; it does not tell you what to remember.
 * ==============================================================================
 */
public final class TurnSnapshot {

    private final Player player;
    private final Room room;

    private final int hp, gold, xp;
    private final List<Item> inventory;
    private final List<Monster> monsters;
    private final List<Item> floorItems;
    private final List<Item> chestContents;

    private final Map<Monster, Integer> monsterHp = new IdentityHashMap<>();
    private final Map<Monster, CombatStrategy> monsterStrategies = new IdentityHashMap<>();
    private final Map<Monster, MonsterState> monsterStates = new IdentityHashMap<>();

    private TurnSnapshot(Player player, Room room) {
        this.player = player;
        this.room = room;

        this.hp = player.getHp();
        this.gold = player.getGold();
        this.xp = player.getXp();
        this.inventory = new ArrayList<>(player.getInventory());

        this.monsters = new ArrayList<>(room.getMonsters());
        this.floorItems = new ArrayList<>(room.getFloorItems());
        this.chestContents = room.getChest() == null
                ? null : new ArrayList<>(room.getChest().getContents());

        for (Monster m : room.getMonsters()) {
            monsterHp.put(m, m.getHp());
            monsterStrategies.put(m, m.getStrategy());
            monsterStates.put(m, m.getState());
        }
    }

    public static TurnSnapshot capture(Player player, Room room) {
        return new TurnSnapshot(player, room);
    }

    public void restore() {
        player.setHp(hp);
        player.setGold(gold);
        player.setXp(xp);
        player.getInventory().clear();
        player.getInventory().addAll(inventory);

        room.getMonsters().clear();
        room.getMonsters().addAll(monsters);          // brings back anything that fled
        room.getFloorItems().clear();
        room.getFloorItems().addAll(floorItems);
        if (chestContents != null && room.getChest() != null) {
            room.getChest().getContents().clear();
            room.getChest().getContents().addAll(chestContents);
        }

        for (Map.Entry<Monster, Integer> e : monsterHp.entrySet()) {
            e.getKey().setHp(e.getValue());
        }
        for (Map.Entry<Monster, CombatStrategy> e : monsterStrategies.entrySet()) {
            e.getKey().setStrategy(e.getValue());     // un-panics anything that turned skittish
        }
        for (Map.Entry<Monster, MonsterState> e : monsterStates.entrySet()) {
            e.getKey().restoreState(e.getValue());    // un-notices, un-enrages, un-kills
        }
    }

    public Room getRoom() { return room; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\commands\UseItemCommand.java
```java
package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;

/**
 * WEEK 7 -- COMMAND, and the home of US-5.4.
 *
 * The Chronomancer's Hourglass calls history.undoLast(). It is a game feature that exists
 * ONLY because actions are objects that remember their own before-state. In the Week 6
 * codebase it could not have been written at all -- there was nothing to rewind.
 *
 * That is the difference between a pattern being tidy and a pattern being load-bearing.
 */
public class UseItemCommand extends AbstractCommand {

    private boolean rewound;

    public UseItemCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    /** A rewind is not itself a turn, so this command's turn-ending-ness depends on what it did. */
    @Override public boolean endsTurn() { return !rewound; }

    @Override
    protected boolean doExecute() {
        Item item = player().findItem(args);
        if (item == null) {
            bus().message("You have nothing like that to use.");
            return false;
        }

        if (item instanceof Hourglass hourglass) {
            if (!hourglass.hasCharges()) {
                bus().message("The hourglass is spent. The sand will not run again.");
                return false;
            }
            hourglass.spendCharge();
            rewound = true;
            bus().message("Sand runs backwards. The last moment unhappens.");
            if (!ctx.getHistory().undoLast()) {
                bus().message("...but there was nothing to take back.");
            }
            return false;    // the rewind IS the effect; do not also burn a turn
        }

        if (item instanceof Potion potion) {
            player().heal(potion.getHealAmount());
            player().removeItem(item);
            bus().publish(GameEvent.message("You drink " + potion.getName()
                    + ". HP " + player().getHp() + "/" + player().getMaxHp()));
            return true;
        }

        bus().message("You cannot think of a use for " + item.getName() + " right now.");
        return false;
    }

    @Override public String getDescription() { return ("use " + args).trim(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\config\GameConfig.java
```java
package dungeonforge.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WEEK 3 -- SINGLETON.  (US-1.1)
 *
 * Exactly one settings source for the whole game. The PRIVATE CONSTRUCTOR plus the STATIC
 * ACCESSOR is what enforces that; a public static field would only ask nicely.
 *
 * Why this deserves to be a Singleton -- the honest version:
 *   It is not "because settings are used everywhere." Lots of things are used everywhere and
 *   should still be passed as parameters. It is because a SECOND GameConfig would be a
 *   genuine bug: two parts of the game disagreeing about how much HP a player starts with is
 *   not a preference, it is a defect. The class enforces a rule that must not be broken.
 *
 * Honest cost, and you should be able to state it:
 *   This introduces global state. It makes testing harder -- see resetForTests() below,
 *   which exists ONLY because the pattern made the class hard to test. That method is a
 *   smell, and it is the price of the pattern.
 *
 * This project permits exactly TWO singletons: this one and RandomSource. If you can pass a
 * thing in as a parameter, it is not a Singleton -- it is a dependency.
 */
public final class GameConfig {

    private static GameConfig instance;

    private final Map<String, Object> settings = new LinkedHashMap<>();

    /** PRIVATE. This is the line that makes the pattern work. */
    private GameConfig() {
        loadDefaults();
        loadFromClasspath("config.json");
    }

    /**
     * Lazy initialisation: the instance is not built until somebody asks.
     * `synchronized` makes this safe if the game ever becomes multi-threaded. Our game is
     * single-threaded, so this is insurance rather than necessity -- see the Week 3 notes on
     * why we did NOT use double-checked locking.
     */
    public static synchronized GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    /** AC4: documented defaults, so a missing or unreadable config file cannot stop the game. */
    private void loadDefaults() {
        settings.put("playerStartingHp", 60.0);
        settings.put("playerStartingAttack", 8.0);
        settings.put("playerStartingDefense", 2.0);
        settings.put("carryCapacity", 60.0);
        settings.put("dungeonDepth", 3.0);
        settings.put("roomsPerLevel", 8.0);
        settings.put("maxMonstersPerRoom", 2.0);
        settings.put("restHealPerRoom", 8.0);
        settings.put("fleeThreshold", 0.30);
        settings.put("enrageThreshold", 0.45);
        settings.put("undoDepth", 25.0);
        settings.put("seed", 20260818.0);
    }

    /** AC1 and AC4: overlay whatever the file provides; keep defaults for anything missing. */
    private void loadFromClasspath(String resourceName) {
        String text = readResource(resourceName);
        if (text == null) return;                         // no file -> defaults stand
        try {
            settings.putAll(Json.parseObject(text));
        } catch (RuntimeException e) {
            System.err.println("[config] " + resourceName + " is malformed; using defaults.");
        }
    }

    /**
     * WEEK 4 -- made public so the MonsterFactory can load monsters.json the same way.
     * Reading a classpath resource is a utility, not a configuration concern, and this is the
     * smallest honest place to put it for now.
     */
    public static String readResource(String resourceName) {
        try (InputStream in = GameConfig.class.getResourceAsStream("/data/" + resourceName)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[config] could not read " + resourceName);
            return null;
        }
    }

    public int getInt(String key) {
        Object v = settings.get(key);
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public double getDouble(String key) {
        Object v = settings.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : 0;
    }

    public long getSeed() { return (long) getDouble("seed"); }

    /**
     * TEST HOOK ONLY. Never call this from game code.
     *
     * Be honest about what this is: the Singleton made this class hard to test, because a
     * test that changes a setting would leak into the next test. This method exists to undo
     * that. A design that needs a special method just so it can be tested is telling you
     * something, and Week 12's Facade will start to offer alternatives.
     */
    public static void resetForTests() { instance = null; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\config\Json.java
```java
package dungeonforge.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tiny dependency-free JSON reader. Not a design pattern and not the point of this week --
 * it exists so the project has zero external dependencies and you can run it the moment you
 * clone it. Read it if you're curious; you do not need to modify it.
 */
public final class Json {

    private final String s;
    private int i;

    private Json(String s) { this.s = s; }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String text) {
        Json p = new Json(text);
        p.ws();
        return (Map<String, Object>) p.value();
    }

    private void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }

    private Object value() {
        switch (s.charAt(i)) {
            case '{': return object();
            case '[': return array();
            case '"': return string();
            case 't': i += 4; return Boolean.TRUE;
            case 'f': i += 5; return Boolean.FALSE;
            case 'n': i += 4; return null;
            default:  return number();
        }
    }

    private Map<String, Object> object() {
        Map<String, Object> m = new LinkedHashMap<>();
        i++; ws();
        if (s.charAt(i) == '}') { i++; return m; }
        while (true) {
            ws();
            String k = string();
            ws(); i++; ws();          // skip ':'
            m.put(k, value());
            ws();
            if (s.charAt(i++) == '}') return m;
        }
    }

    private List<Object> array() {
        List<Object> l = new ArrayList<>();
        i++; ws();
        if (s.charAt(i) == ']') { i++; return l; }
        while (true) {
            ws(); l.add(value()); ws();
            if (s.charAt(i++) == ']') return l;
        }
    }

    private String string() {
        StringBuilder b = new StringBuilder();
        i++;
        while (true) {
            char c = s.charAt(i++);
            if (c == '"') return b.toString();
            if (c == '\\') {
                char e = s.charAt(i++);
                switch (e) {
                    case 'n': b.append('\n'); break;
                    case 't': b.append('\t'); break;
                    case 'r': b.append('\r'); break;
                    default:  b.append(e);
                }
            } else {
                b.append(c);
            }
        }
    }

    private Double number() {
        int start = i;
        while (i < s.length() && "-+.eE0123456789".indexOf(s.charAt(i)) >= 0) i++;
        return Double.valueOf(s.substring(start, i));
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\config\RandomSource.java
```java
package dungeonforge.config;

import java.util.List;
import java.util.Random;

/**
 * WEEK 3 -- SINGLETON.  (US-1.2)
 *
 * This is the singleton with the STRONGEST justification in the whole project, and it is the
 * one to point at when someone says Singleton is always an anti-pattern.
 *
 * One RNG plus one seed means the same dungeon every run. That gives you:
 *   - reproducible bug reports  ("seed 12345, level 2, the boss room is empty")
 *   - deterministic tests       (a test that generates a level gets the same level every time)
 *   - a save file that stores a SEED instead of a whole dungeon layout  (you will use this in Week 12)
 *
 * Two RNGs would silently destroy all three. That is a correctness requirement, not a
 * convenience -- which is exactly the bar a Singleton has to clear.
 */
public final class RandomSource {

    private static RandomSource instance;

    private Random rng;
    private long seed;

    private RandomSource() {
        this.seed = GameConfig.getInstance().getSeed();
        this.rng = new Random(seed);
    }

    public static synchronized RandomSource getInstance() {
        if (instance == null) {
            instance = new RandomSource();
        }
        return instance;
    }

    public int nextInt(int bound)      { return bound <= 0 ? 0 : rng.nextInt(bound); }
    public int between(int lo, int hi) { return hi <= lo ? lo : lo + rng.nextInt(hi - lo + 1); }
    public double nextDouble()         { return rng.nextDouble(); }
    public <T> T pick(List<T> options) { return options.isEmpty() ? null : options.get(rng.nextInt(options.size())); }
    public <T> T pick(T[] options)     { return options.length == 0 ? null : options[rng.nextInt(options.length)]; }

    public long getSeed() { return seed; }

    /** Lets a test -- or a --seed flag -- ask for a specific world. AC4 of US-1.2. */
    public void reseed(long newSeed) {
        this.seed = newSeed;
        this.rng = new Random(newSeed);
    }

    /** TEST HOOK ONLY. Same caveat as GameConfig.resetForTests(). */
    public static void resetForTests() { instance = null; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\Combat.java
```java
package dungeonforge.core;

import dungeonforge.behavior.Action;
import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.CombatStrategy;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.states.DeadState;
import dungeonforge.states.MonsterState;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- strategies decide HOW a monster fights.
 * WEEK 7 -- one round at a time, because a human is typing.
 *
 * TODO(week 8, US-6.3): now look at what happened to this class.
 *
 * A monster has a LIFECYCLE -- idle, hunting, enraged, fleeing, dead -- and that lifecycle is
 * currently expressed as a String field plus conditionals in THREE separate places below:
 *
 *   updateMonsterState()  the transitions themselves, as nested ifs
 *   monsterActs()         "can this monster act at all?"
 *   damageFrom()          "is it enraged, and therefore hitting harder?"
 *
 * Chapter 10 of your textbook opens with exactly this design and then dismantles it. Before
 * you start, answer these in docs/evidence.md:
 *
 */
public class Combat {

    private final EventBus bus;

    public Combat(EventBus bus) { this.bus = bus; }

    /** One round of MONSTER actions. */
    public void monsterTurns(Player player, Room room) {
        for (Monster m : livingMonsters(room)) {
            monsterActs(m, player, room);

            MonsterState before = m.getState();
            CombatStrategy tacticsBefore = m.getStrategy();

            m.setState(before.next(m, player));

            if (m.getStrategy() != tacticsBefore) {
                bus.publish(GameEvent.of(EventType.STRATEGY_CHANGED,
                        "name", m.getName(), "from", tacticsBefore.name(), "to", m.getStrategy().name()));
            }

            if (m.getState() != before) {
                bus.publish(GameEvent.of(EventType.MONSTER_STATE_CHANGED,
                        "name", m.getName(),
                        "from", before.label(),
                        "to", m.getState().label()));
            }

            if (!player.isAlive()) break;
        }
        if (!player.isAlive()) {
            bus.publish(GameEvent.of(EventType.PLAYER_DIED));
        } else if (!hasLiving(room)) {
            bus.publish(GameEvent.of(EventType.ROOM_CLEARED, "room", room.getId()));
        }
    }

    private void monsterActs(Monster m, Player player, Room room) {
        if (!m.getState().canAct()) return;
        if (m.getStrategy() == null) return;

        Action action = m.getStrategy().chooseAction(m, player, room);
        if (action == null) return;

        switch (action.getType()) {
            case ATTACK -> {
                int dmg = damageFrom(m);
                player.takeDamage(dmg);
                bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN,
                        "source", m.getName(), "amount", dmg, "flavor", action.getFlavor()));
            }
            case RANGED_ATTACK -> {
                int dmg = Math.max(1, (int) Math.round(damageFrom(m) * 0.8));
                player.takeDamage(dmg);
                bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN,
                        "source", m.getName(), "amount", dmg, "flavor", action.getFlavor()));
            }
            case FLEE -> {
                room.getMonsters().remove(m);
                bus.publish(GameEvent.of(EventType.MONSTER_FLED, "name", m.getName()));
            }
            case HEAL_ALLY -> {
                if (action.getTarget() != null) {
                    action.getTarget().heal(5);
                    bus.publish(GameEvent.of(EventType.MONSTER_HEALED,
                            "healer", m.getName(), "target", action.getTarget().getName()));
                }
            }
            case WAIT -> bus.publish(GameEvent.message(action.getFlavor()));
        }
    }

    private int damageFrom(Monster m) {
        return Math.max(1, (int)Math.round(m.getAttackPower() * m.getState().damageMultiplier())); }

    public void playerStrikes(Player player, Monster target) {
        int damage = player.getAttackPower();
        target.takeDamage(damage);
        bus.publish(GameEvent.of(EventType.DAMAGE_DEALT,
                "target", target.getName(), "amount", damage));

        if (!target.isAlive()) {
            target.setState(new DeadState());
            player.addXp(target.getXpReward());
            player.addGold(target.getXpReward() * 2);
            bus.publish(GameEvent.of(EventType.MONSTER_DIED,
                    "name", target.getName(), "xp", target.getXpReward()));
            bus.publish(GameEvent.of(EventType.XP_GAINED, "amount", target.getXpReward()));
            bus.publish(GameEvent.of(EventType.GOLD_GAINED, "amount", target.getXpReward() * 2));
        }
    }

    public Monster firstLivingIn(Room room) { return firstLiving(room); }

    private boolean hasLiving(Room room) { return firstLiving(room) != null; }

    private Monster firstLiving(Room room) {
        for (Monster m : room.getMonsters()) if (m.isAlive()) return m;
        return null;
    }

    private List<Monster> livingMonsters(Room room) {
        List<Monster> out = new ArrayList<>();
        for (Monster m : room.getMonsters()) if (m.isAlive()) out.add(m);
        return out;
    }

    /** Between rooms the player catches their breath. The dead do not catch their breath. */
    public static void restAfterRoom(Player player) {
        if (!player.isAlive()) return;
        player.heal(GameConfig.getInstance().getInt("restHealPerRoom"));
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\DungeonLevel.java
```java
package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- one level of the dungeon.
 * WEEK 4 -- a level now knows what KIND of place it is. It stores the theme's NAME rather
 * than the kit itself, because a level is a piece of world state, not a factory.
 */
public class DungeonLevel {

    private final int depth;
    private final String themeName;
    private final List<Room> rooms = new ArrayList<>();

    public DungeonLevel(int depth, String themeName) {
        this.depth = depth;
        this.themeName = themeName;
    }

    public int getDepth()        { return depth; }
    public String getThemeName() { return themeName; }
    public List<Room> getRooms() { return rooms; }
    public void addRoom(Room r)  { rooms.add(r); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\Entity.java
```java
package dungeonforge.core;

/**
 * WEEK 1 -- the shared contract for anything that can fight.
 *
 * Nothing wrong with this class. It is here because Player and Monster extend it.
 */
public abstract class Entity {

    protected final String name;
    protected int hp;
    protected int maxHp;
    protected int attackPower;
    protected int defense;

    protected Entity(String name, int maxHp, int attackPower, int defense) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackPower = attackPower;
        this.defense = defense;
    }

    public String getName()      { return name; }
    public int getHp()           { return hp; }
    public int getMaxHp()        { return maxHp; }
    public int getAttackPower()  { return attackPower; }
    public int getDefense()      { return defense; }
    public boolean isAlive()     { return hp > 0; }

    /**
     * WEEK 7: needed so a TurnSnapshot can put hit points back during undo.
     * Clamped, so no snapshot can ever restore an impossible value.
     */
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }

    public void takeDamage(int amount) { hp = Math.max(0, hp - Math.max(0, amount)); }
    public void heal(int amount)       { hp = Math.min(maxHp, hp + Math.max(0, amount)); }

    public abstract String describe();

    @Override public String toString() { return name; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\GameWorld.java
```java
package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.factory.BossRoomPopulator;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.RoomPopulator;
import dungeonforge.factory.StandardRoomPopulator;
import dungeonforge.factory.ThemeKit;
import dungeonforge.factory.ThemeRegistry;
import dungeonforge.factory.TreasureRoomPopulator;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- the world.
 * WEEK 3 -- configuration and randomness come from singletons.
 * WEEK 4 -- creation is gone.
 *
 * Read this class and notice what is ABSENT. There is no `new Monster(`. There is no species
 * list. There are no stat formulas. There is no flavour text. GameWorld no longer knows that
 * skeletons exist, and adding a fourth theme will not bring it back into the conversation.
 *
 * That absence is the deliverable. "Identify the aspects that vary and separate them from
 * what stays the same" -- what stays the same is the LOOP; what varies is everything the
 * loop puts inside a room.
 */
public class GameWorld {

    private final Player player;
    private final MonsterFactory monsterFactory = new MonsterFactory();
    private final ThemeRegistry themes = new ThemeRegistry(monsterFactory);
    private final List<DungeonLevel> levels = new ArrayList<>();

    public GameWorld(Player player) {
        this.player = player;
        generate();
    }

    private void generate() {
        GameConfig cfg = GameConfig.getInstance();
        int dungeonDepth = cfg.getInt("dungeonDepth");
        int roomsPerLevel = cfg.getInt("roomsPerLevel");

        for (int d = 1; d <= dungeonDepth; d++) {
            ThemeKit theme = themes.forDepth(d);              // ABSTRACT FACTORY
            DungeonLevel level = new DungeonLevel(d, theme.themeName());

            for (int r = 0; r < roomsPerLevel; r++) {
                Room room = new Room("L" + d + "R" + r);
                boolean isEntrance = (r == 0);
                boolean isFinalRoom = (d == dungeonDepth && r == roomsPerLevel - 1);

                if (isEntrance) {
                    room.setFlavor(theme.createRoomFlavor());  // safe: no encounter
                } else {
                    populatorFor(theme, isFinalRoom).populate(room, d);   // FACTORY METHOD
                }
                level.addRoom(room);
            }
            levels.add(level);
        }
        linkRooms();
    }

    /**
     * WEEK 7 (provided): wire the rooms into something walkable.
     *
     * Deliberately a straight corridor -- "north" goes deeper, "south" goes back, and the last
     * room of a level has "down" to the next level. Week 10's generators replace this with a
     * real graph. It is here so that "move" is a genuine action this week.
     */
    private void linkRooms() {
        for (int d = 0; d < levels.size(); d++) {
            List<Room> rooms = levels.get(d).getRooms();
            for (int r = 0; r + 1 < rooms.size(); r++) {
                rooms.get(r).link("north", rooms.get(r + 1));
                rooms.get(r + 1).link("south", rooms.get(r));
            }
            if (d + 1 < levels.size()) {
                rooms.get(rooms.size() - 1).link("down", levels.get(d + 1).getRooms().get(0));
            }
        }
    }

    public Room startingRoom() { return levels.get(0).getRooms().get(0); }

    public DungeonLevel levelContaining(Room room) {
        for (DungeonLevel l : levels) {
            if (l.getRooms().contains(room)) return l;
        }
        return levels.get(0);
    }

    /**
     * The only place that chooses BETWEEN populators. Note that it returns the abstract type,
     * so everything downstream is written against RoomPopulator and never against a subclass.
     */
    private RoomPopulator populatorFor(ThemeKit theme, boolean isFinalRoom) {
        if (isFinalRoom) return new BossRoomPopulator(theme);
        if (RandomSource.getInstance().nextDouble() < 0.30) return new TreasureRoomPopulator(theme);
        return new StandardRoomPopulator(theme);
    }

    public Player getPlayer()             { return player; }
    public List<DungeonLevel> getLevels() { return levels; }
    public MonsterFactory getMonsterFactory() { return monsterFactory; }
    public ThemeRegistry getThemes()      { return themes; }

    public int totalMonsters() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) n += r.getMonsters().size();
        }
        return n;
    }

    public int totalLoot() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) {
                if (r.hasChest()) n += r.getChest().getContents().size();
            }
        }
        return n;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\Monster.java
```java
package dungeonforge.core;

import dungeonforge.behavior.CombatStrategy;
import dungeonforge.config.RandomSource;
import dungeonforge.states.IdleState;
import dungeonforge.states.MonsterState;

/**
 * WEEK 1 -- a monster.
 * WEEK 3 -- stat variance comes from the one seeded RandomSource.
 * WEEK 5 -- a monster HAS-A CombatStrategy. It does not IS-A anything behavioural.
 *
 * That one field is the whole Strategy pattern. Because behaviour is a thing the monster
 * HOLDS, it can be handed a different one mid-fight -- see Combat.checkForTacticsChange().
 * If behaviour were expressed through subclassing, that swap would be impossible: you cannot
 * change an object's class at runtime.
 */
public class Monster extends Entity {

    private final String species;
    private final int xpReward;
    private CombatStrategy strategy;

    /**
     * TODO(week 8, US-6.3): a monster's LIFECYCLE, stored as a string.
     *
     * "idle" -> "hunting" -> "enraged" -> "fleeing" -> "dead"
     *
     * This is exactly how the Gumball Machine starts in Chapter 10 of your textbook: a field
     * holding the current state, and conditionals about that field scattered through every
     * method that cares. Grep Combat.java for getStateName() before you start, and count.
     */

    private MonsterState stateName = new IdleState();

    /**
     * WEEK 5 correction: stat variance used to be rolled here AND again in MonsterFactory,
     * so every monster was randomised twice and no test could build one with exact hit
     * points. Variance is a CREATION concern, so it now lives only in the factory. A Monster
     * is exactly what it was constructed with.
     */
    public Monster(String species, int maxHp, int attackPower, int xpReward) {
        super(species, maxHp, attackPower, 0);
        this.species = species;
        this.xpReward = xpReward;
    }

    public String getSpecies() { return species; }
    public int getXpReward()   { return xpReward; }

    // --- WEEK 5: STRATEGY, held by composition and swappable at runtime ---
    public CombatStrategy getStrategy()              { return strategy; }
    public void setStrategy(CombatStrategy strategy) { this.strategy = strategy; }

    public MonsterState getState()          { return stateName; }

    public void setState(MonsterState next) {
        if (next == null || next == this.stateName) return;
        this.stateName = next;
        next.onEnter(this);
    }

    /**
     * Only TurnSnapshot should call this. ANything else is a transition and belongs above.
     */
    public void restoreState(MonsterState previous) {
        if (previous != null) this.stateName = previous;
    }
    /** How close to death, as a fraction. Strategies read this to decide whether to run. */
    public double hpFraction() { return maxHp == 0 ? 0 : (double) hp / maxHp; }

    @Override
    public String describe() {
        return species + " (" + hp + "/" + maxHp + " HP, ATK " + attackPower
                + ", " + stateName + ")";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\Player.java
```java
package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- the player.
 * WEEK 3 (US-1.1) -- every tunable number now comes from GameConfig. There is not one
 * numeric literal left in this class, which is what acceptance criterion 2 demanded.
 */
public class Player extends Entity {

    private int gold;
    private int xp;
    /**
     * WEEK 7 (provided): a plain list, deliberately.
     * Week 13 turns this into a COMPOSITE so bags can hold bags. Do not build that now.
     */
    private final List<Item> inventory = new ArrayList<>();

    public Player(String name) {
        super(name,
              GameConfig.getInstance().getInt("playerStartingHp"),
              GameConfig.getInstance().getInt("playerStartingAttack"),
              GameConfig.getInstance().getInt("playerStartingDefense"));
    }

    public List<Item> getInventory()   { return inventory; }
    public void addItem(Item i)        { inventory.add(i); }
    public boolean removeItem(Item i)  { return inventory.remove(i); }

    /** Case-insensitive partial match, so "use draught" finds "Quench Draught". */
    public Item findItem(String query) {
        if (query == null || query.isBlank()) return null;
        String q = query.toLowerCase();
        for (Item i : inventory) {
            if (i.getName().toLowerCase().contains(q)) return i;
        }
        return null;
    }

    public void setGold(int g)    { this.gold = Math.max(0, g); }
    public void setXp(int x)      { this.xp = Math.max(0, x); }

    public int getGold()          { return gold; }
    public int getXp()            { return xp; }
    public void addGold(int g)    { gold += g; }
    public void addXp(int x)      { xp += x; }

    /** Backpack capacity in kilograms, from config. */
    public double carryCapacity() {
        return GameConfig.getInstance().getDouble("carryCapacity");
    }

    @Override
    public String describe() {
        return name + "  HP " + hp + "/" + maxHp
                + "  ATK " + attackPower + "  DEF " + defense
                + "  Gold " + gold + "  XP " + xp
                + "  Carry " + carryCapacity() + "kg";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\core\Room.java
```java
package dungeonforge.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import dungeonforge.items.Chest;
import dungeonforge.items.Item;

/**
 * WEEK 1 -- one room of the dungeon.
 * WEEK 3 (US-1.2) -- flavour text is picked from the one seeded source.
 */
public class Room {

    private final String id;
    private String flavor;
    private final List<Monster> monsters = new ArrayList<>();
    private final List<Item> floorItems = new ArrayList<>();
    private Chest chest;
    /** WEEK 7 (provided): where you can walk from here. Week 10's generators make this a real graph. */
    private final Map<String, Room> exits = new LinkedHashMap<>();

    public Room(String id) {
        this.id = id;
        this.flavor = "";
    }

    public String getId()               { return id; }
    public String getFlavor()           { return flavor; }
    public void setFlavor(String f)     { this.flavor = f; }
    public List<Monster> getMonsters()  { return monsters; }
    public void addMonster(Monster m)   { monsters.add(m); }
    public List<Item> getFloorItems()   { return floorItems; }
    public void addItem(Item i)         { floorItems.add(i); }
    public Chest getChest()             { return chest; }
    public void setChest(Chest c)       { this.chest = c; }
    public boolean hasChest()           { return chest != null && !chest.isEmpty(); }

    public Map<String, Room> getExits()      { return exits; }
    public Room getExit(String direction)    { return exits.get(direction.toLowerCase()); }
    public void link(String direction, Room to) { exits.put(direction.toLowerCase(), to); }
    /** The first monster still standing, or null. */
    public Monster firstLivingIn() {
        for (Monster m : monsters) if (m.isAlive()) return m;
        return null;
    }

    public boolean hasLivingMonsters() {
        for (Monster m : monsters) if (m.isAlive()) return true;
        return false;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\AchievementSystem.java
```java
package dungeonforge.events;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * WEEK 5 -- OBSERVER, and THE GRADED PROOF of US-3.4.
 *
 * This class was written AFTER combat was finished, and adding it required editing nothing.
 * One new class, one subscribe() call. If you had to touch Combat to make achievements work,
 * your Observer wiring has a leak and the story is not done.
 */
public class AchievementSystem implements GameEventListener {

    private final Set<String> unlocked = new LinkedHashSet<>();
    private final EventBus bus;

    private int kills;
    private int fled;
    private int deepestLevel;

    public AchievementSystem(EventBus bus) { this.bus = bus; }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case MONSTER_DIED -> {
                kills++;
                if (kills == 1)  unlock("First Blood", "Defeat your first monster");
                if (kills == 10) unlock("Exterminator", "Defeat 10 monsters");
            }
            case MONSTER_FLED -> {
                fled++;
                if (fled == 1) unlock("Terrifying", "Make something run away from you");
            }
            case STRATEGY_CHANGED ->
                unlock("Cornered", "Force an enemy to change tactics");
            case LEVEL_ENTERED -> {
                deepestLevel = Math.max(deepestLevel, event.getInt("depth"));
                if (deepestLevel >= 3) unlock("Deep Delver", "Reach dungeon level 3");
            }
            case DELVE_SURVIVED ->
                unlock("Survivor", "Walk out of the dungeon alive");
            default -> { }
        }
    }

    private void unlock(String name, String description) {
        if (unlocked.add(name)) {
            bus.message("ACHIEVEMENT: " + name + " -- " + description);
        }
    }

    public Set<String> getUnlocked() { return unlocked; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\CombatLog.java
```java
package dungeonforge.events;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * WEEK 5 -- OBSERVER, third listener, also added with zero publisher changes.
 *
 * Note what it does NOT do: it does not print. It turns events into text and holds the last
 * N lines. Deciding where text goes is a VIEW's job, and Week 15 will make that split
 * official. Keeping it clean now is what makes the optional GUI possible later.
 */
public class CombatLog implements GameEventListener {

    private final Deque<String> lines = new ArrayDeque<>();
    private final int maxLines;

    public CombatLog(int maxLines) { this.maxLines = maxLines; }

    @Override
    public void onEvent(GameEvent event) {
        String line = format(event);
        if (line == null) return;
        lines.addLast(line);
        while (lines.size() > maxLines) lines.removeFirst();
    }

    private String format(GameEvent e) {
        return switch (e.getType()) {
            case MESSAGE          -> e.getString("text");
            case DAMAGE_DEALT     -> "You hit " + e.getString("target") + " for " + e.getInt("amount") + ".";
            case DAMAGE_TAKEN     -> e.getString("source") + " hits you for " + e.getInt("amount") + ".";
            case MONSTER_DIED     -> e.getString("name") + " dies. +" + e.getInt("xp") + " XP.";
            case MONSTER_FLED     -> e.getString("name") + " flees into the dark.";
            case MONSTER_HEALED   -> e.getString("healer") + " mends " + e.getString("target") + ".";
            case MONSTER_STATE_CHANGED -> e.getString("name") + " is now "
                                + e.getString("to") + ".";
            case STRATEGY_CHANGED -> e.getString("name") + " changes tactics: "
                                     + e.getString("from") + " -> " + e.getString("to") + ".";
            case ROOM_CLEARED     -> "Room " + e.getString("room") + " is quiet.";
            case LEVEL_ENTERED    -> "== Level " + e.getInt("depth") + ": " + e.getString("theme") + " ==";
            case PLAYER_DIED      -> "You die in the dark.";
            case DELVE_SURVIVED   -> "You climb back into daylight.";
            default               -> null;
        };
    }

    public List<String> getLines() { return new ArrayList<>(lines); }

    /** Used by one-line "print the newest event" views so stale lines are not re-shown. */
    public void clear() { lines.clear(); }
    public int size()              { return lines.size(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\DangerMeter.java
```java
package dungeonforge.events;

import dungeonforge.core.Player;

public class DangerMeter implements GameEventListener {

    private final Player player;
    private final double threshold;
    private boolean warned = false;

    public DangerMeter(EventBus bus, Player player, double threshold) {
        this.player = player;
        this.threshold = threshold;
    }

    @Override
    public void onEvent(GameEvent event) {

        double healthFraction =
                (double) player.getHp() / player.getMaxHp();

        if (healthFraction <= threshold && !warned) {
            System.out.println("DANGER! Your health is critically low!");
            warned = true;
        }

        if (healthFraction > threshold) {
            warned = false;
        }
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\EventBus.java
```java
package dungeonforge.events;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- OBSERVER (the Subject).
 *
 * ============================== THE POINT ==============================
 * Combat publishes. It has ZERO references to QuestTracker, AchievementSystem or CombatLog,
 * and it never will. Adding a fourth listener requires no change to any publisher.
 *
 * Part D2 asks you to prove that with `git diff --stat` rather than take my word for it.
 * =======================================================================
 *
 * "Define a one-to-many dependency between objects so that when one object changes state,
 *  all of its dependents are notified and updated automatically."
 *
 * Design principle: strive for LOOSELY COUPLED designs between objects that interact. The
 * only thing the two sides share is GameEvent.
 */
public final class EventBus {

    private final List<GameEventListener> listeners = new ArrayList<>();

    public void subscribe(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Iterating a COPY matters. A listener is allowed to unsubscribe itself while being
     * notified, and without the copy that would throw ConcurrentModificationException.
     * This is the bug students hit first, and it is worth hitting once on purpose.
     */
    public void publish(GameEvent event) {
        for (GameEventListener listener : new ArrayList<>(listeners)) {
            listener.onEvent(event);
        }
    }

    public void message(String text) { publish(GameEvent.message(text)); }

    public int listenerCount() { return listeners.size(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\EventType.java
```java
package dungeonforge.events;

/** WEEK 5 -- OBSERVER. The vocabulary the model publishes. */
public enum EventType {
    DAMAGE_DEALT,
    DAMAGE_TAKEN,
    MONSTER_DIED,
    MONSTER_FLED,
    MONSTER_HEALED,
    MONSTER_STATE_CHANGED,
    STRATEGY_CHANGED,
    XP_GAINED,
    GOLD_GAINED,
    LEVEL_ENTERED,
    ROOM_CLEARED,
    PLAYER_DIED,
    DELVE_SURVIVED,
    MESSAGE
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\GameEvent.java
```java
package dungeonforge.events;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WEEK 5 -- OBSERVER.
 *
 * A notification, not a command. Subscribers READ it; nothing here lets them change the game.
 * That read-only quality is what makes it safe to have four unrelated listeners on one bus.
 */
public final class GameEvent {

    private final EventType type;
    private final Map<String, Object> payload = new LinkedHashMap<>();

    private GameEvent(EventType type) { this.type = type; }

    /** of(TYPE, "key", value, "key", value, ...) */
    public static GameEvent of(EventType type, Object... keyValuePairs) {
        GameEvent e = new GameEvent(type);
        for (int i = 0; i + 1 < keyValuePairs.length; i += 2) {
            e.payload.put(String.valueOf(keyValuePairs[i]), keyValuePairs[i + 1]);
        }
        return e;
    }

    public static GameEvent message(String text) {
        return of(EventType.MESSAGE, "text", text);
    }

    public EventType getType()         { return type; }
    public Object get(String key)      { return payload.get(key); }

    public String getString(String key) {
        Object o = payload.get(key);
        return o == null ? "" : String.valueOf(o);
    }

    public int getInt(String key) {
        Object o = payload.get(key);
        return o instanceof Number ? ((Number) o).intValue() : 0;
    }

    @Override public String toString() { return type + payload.toString(); }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\GameEventListener.java
```java
package dungeonforge.events;

/**
 * WEEK 5 -- OBSERVER.
 *
 * The entire contract is one method. Implement it, subscribe, and you get told about
 * everything. That is deliberately tiny: the cheaper it is to become a listener, the more
 * likely it is that new features get added as listeners instead of as edits to Combat.
 */
public interface GameEventListener {
    void onEvent(GameEvent event);
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\Quest.java
```java
package dungeonforge.events;

/** WEEK 5 -- plain data for the QuestTracker. Not a pattern; just a countable goal. */
public class Quest {

    private final String id;
    private final String description;
    private final EventType trigger;
    private final int required;
    private int progress;

    public Quest(String id, String description, EventType trigger, int required) {
        this.id = id;
        this.description = description;
        this.trigger = trigger;
        this.required = required;
    }

    public String getId()          { return id; }
    public String getDescription() { return description; }
    public EventType getTrigger()  { return trigger; }
    public int getRequired()       { return required; }
    public int getProgress()       { return progress; }
    public boolean isComplete()    { return progress >= required; }

    /** Returns true on the tick that COMPLETES the quest, so the caller can announce it once. */
    public boolean advance() {
        if (isComplete()) return false;
        progress++;
        return isComplete();
    }

    @Override
    public String toString() {
        return (isComplete() ? "[x] " : "[ ] ") + description
                + " (" + Math.min(progress, required) + "/" + required + ")";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\events\QuestTracker.java
```java
package dungeonforge.events;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- OBSERVER (a concrete observer).
 *
 * Read Combat.java and search it for the word "quest". There are no matches, and there never
 * need to be. Quests are a whole game system that combat has never heard of.
 */
public class QuestTracker implements GameEventListener {

    private final List<Quest> quests = new ArrayList<>();
    private final EventBus bus;

    public QuestTracker(EventBus bus) {
        this.bus = bus;
        quests.add(new Quest("q1", "Slay 5 creatures of the dungeon", EventType.MONSTER_DIED, 5));
        quests.add(new Quest("q2", "Drive off 2 enemies", EventType.MONSTER_FLED, 2));
        quests.add(new Quest("q3", "Clear 6 rooms", EventType.ROOM_CLEARED, 6));
    }

    @Override
    public void onEvent(GameEvent event) {
        for (Quest q : quests) {
            if (q.getTrigger() != event.getType()) continue;
            if (q.advance()) {
                bus.message("QUEST COMPLETE: " + q.getDescription());
            }
        }
    }

    public List<Quest> getQuests() { return quests; }

    public int completedCount() {
        int n = 0;
        for (Quest q : quests) if (q.isComplete()) n++;
        return n;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\BossRoomPopulator.java
```java
package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.items.Chest;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD. One boss, one escort, one hoard.
 *
 * Notice that this class never names a monster. It asks the ThemeKit for "the boss of this
 * kind of place" and gets a Bone Tyrant in a crypt and a Rime Tyrant in the ice. Factory
 * Method decided HOW MANY; Abstract Factory decided WHICH.
 */
public class BossRoomPopulator extends RoomPopulator {

    public BossRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "boss"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        out.add(theme.createBoss(depth));
        out.add(theme.createMonster(depth));
        return out;
    }

    @Override
    protected Chest createChest(int depth) {
        Chest chest = new Chest("Tyrant's Hoard");
        chest.add(theme.createLoot(depth + 1));
        chest.add(theme.createLoot(depth + 1));
        return chest;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\CryptThemeKit.java
```java
package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Armor;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;
import dungeonforge.items.Treasure;
import dungeonforge.items.Weapon;

/** WEEK 4 -- ABSTRACT FACTORY. Damp stone, the undead, bone charms. */
public class CryptThemeKit implements ThemeKit {

    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
        "Damp stone. Something drips in the dark, patiently.",
        "Burial niches line the walls. Most are empty. Most.",
        "The air tastes of old dust and older grief.",
        "Your footsteps come back a half-second late."
    };

    public CryptThemeKit(MonsterFactory factory) { this.factory = factory; }

    @Override public String themeName() { return "Crypt"; }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("crypt"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("bone_tyrant", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch (RandomSource.getInstance().nextInt(4)) {
            case 0:  return new Weapon("Bone Shortsword", 2.0, 40 + depth * 10, 5 + depth);
            case 1:  return new Armor("Grave Shroud", 3.0, 35 + depth * 8, 2 + depth);
            case 2:  return new Potion("Vial of Still Water", 0.4, 25, 18 + depth * 3);
            default: return new Treasure("Bone Charm", 0.2, 60 + depth * 15);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\ForgeThemeKit.java
```java
package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Armor;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;
import dungeonforge.items.Treasure;
import dungeonforge.items.Weapon;

/** WEEK 4 -- ABSTRACT FACTORY. Heat, imps, fire-forged steel. */
public class ForgeThemeKit implements ThemeKit {

    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
        "Heat rolls off the walls in slow waves.",
        "Cinders drift upward from a grate in the floor.",
        "Somewhere below, a hammer strikes. Nobody is holding it.",
        "The stone here has been melted and re-set, badly."
    };

    public ForgeThemeKit(MonsterFactory factory) { this.factory = factory; }

    @Override public String themeName() { return "Forge"; }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("forge"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("forge_tyrant", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch (RandomSource.getInstance().nextInt(4)) {
            case 0:  return new Weapon("Slag Cleaver", 4.0, 55 + depth * 12, 7 + depth);
            case 1:  return new Armor("Scorched Plate", 6.0, 60 + depth * 10, 4 + depth);
            case 2:  return new Potion("Quench Draught", 0.4, 30, 22 + depth * 3);
            default: return new Treasure("Ingot of Bright Iron", 2.0, 80 + depth * 18);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\FrostThemeKit.java
```java
package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Armor;
import dungeonforge.items.Item;
import dungeonforge.items.Potion;
import dungeonforge.items.Treasure;
import dungeonforge.items.Weapon;

/** WEEK 4 -- ABSTRACT FACTORY. Cold, rime, things that were people. */
public class FrostThemeKit implements ThemeKit {

    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
        "Your breath hangs in the air and does not disperse.",
        "Rime creeps across the floor toward your boots.",
        "It is silent here in a way that feels deliberate.",
        "Ice sheets the walls, and something moves behind it."
    };

    public FrostThemeKit(MonsterFactory factory) { this.factory = factory; }

    @Override public String themeName() { return "Frost"; }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("frost"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("rime_tyrant", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch (RandomSource.getInstance().nextInt(4)) {
            case 0:  return new Weapon("Rime Shard", 1.5, 50 + depth * 11, 6 + depth);
            case 1:  return new Armor("Frostweave Cloak", 2.0, 45 + depth * 9, 3 + depth);
            case 2:  return new Potion("Warming Cordial", 0.4, 28, 20 + depth * 3);
            default: return new Treasure("Frozen Tear", 0.1, 90 + depth * 20);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\MonsterDef.java
```java
package dungeonforge.factory;

/**
 * WEEK 4 -- the blueprint a factory turns into a Monster.
 *
 * Plain data, no behaviour. Its whole job is to let monster CONTENT live in a JSON file
 * instead of in Java, so adding a monster is a data change rather than a code change.
 */
public class MonsterDef {

    public final String id;
    public final String name;
    public final int hp;
    public final int attack;
    public final int xp;
    public final String theme;
    /** Bosses are excluded from ordinary spawns -- a ThemeKit asks for one by name. */
    public final boolean boss;
    /** WEEK 5: which CombatStrategy this species uses by default. Content, not code. */
    public final String strategy;

    public MonsterDef(String id, String name, int hp, int attack, int xp, String theme) {
        this(id, name, hp, attack, xp, theme, false, "aggressive");
    }

    public MonsterDef(String id, String name, int hp, int attack, int xp, String theme,
                      boolean boss, String strategy) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.xp = xp;
        this.theme = theme;
        this.boss = boss;
        this.strategy = strategy;
    }

    @Override
    public String toString() {
        return id + "(" + name + " hp=" + hp + " atk=" + attack + " theme=" + theme
                + (boss ? " BOSS" : "") + ")";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\MonsterFactory.java
```java
package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.Json;
import dungeonforge.config.RandomSource;
import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.CombatStrategy;
import dungeonforge.behavior.HealerStrategy;
import dungeonforge.behavior.RangedStrategy;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.core.Monster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 4 -- SIMPLE FACTORY.
 *
 * ============================ SAY THIS OUT LOUD ============================
 * Simple Factory is NOT one of the Gang of Four patterns. It is a useful IDIOM, and Head
 * First is careful to make that distinction before it teaches Factory Method.
 *
 * What it gives you: construction happens in exactly one place, so `new Monster(...)`
 * appears once in the whole codebase instead of scattered through GameWorld.
 *
 * What it does NOT give you: any way for a caller to VARY what gets created without
 * changing this class. That is what Factory Method adds, and it is why the two are
 * different patterns rather than the same one at different sizes.
 * ==========================================================================
 *
 * Note the registry Map rather than an if/else chain over ids. Adding a monster is a line in
 * monsters.json -- no Java changes at all.
 */
public class MonsterFactory {

    private final Map<String, MonsterDef> blueprints = new LinkedHashMap<>();

    public MonsterFactory() {
        loadFrom("monsters.json");
    }

    /** Reads blueprints from a classpath resource. Content is data, not code. */
    private void loadFrom(String resourceName) {
        String text = GameConfig.readResource(resourceName);
        if (text == null) {
            System.err.println("[factory] " + resourceName + " not found; no monsters registered.");
            return;
        }
        Map<String, Object> root = Json.parseObject(text);
        for (Map.Entry<String, Object> e : root.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) e.getValue();
            register(new MonsterDef(
                    e.getKey(),
                    str(m.get("name"), e.getKey()),
                    num(m.get("hp"), 10),
                    num(m.get("attack"), 3),
                    num(m.get("xp"), 5),
                    str(m.get("theme"), "crypt"),
                    Boolean.TRUE.equals(m.get("boss")),
                    str(m.get("strategy"), "aggressive")));
        }
    }

    public void register(MonsterDef def) { blueprints.put(def.id, def); }

    /**
     * THE factory method of the Simple Factory idiom -- the single place `new Monster` lives.
     * Stats scale with depth and carry a little seeded variance.
     */
    public Monster create(String id, int depth) {
        MonsterDef d = blueprints.get(id);
        if (d == null) {
            // Unknown ids must not crash the game. AC4 of US-2.1.
            d = new MonsterDef(id, "Shambling " + id, 10, 3, 5, "crypt");
        }
        int scale = Math.max(0, depth - 1);
        RandomSource rng = RandomSource.getInstance();
        Monster m = new Monster(
                d.name,
                Math.max(1, d.hp + scale * 4 + rng.between(-2, 2)),
                Math.max(1, d.attack + scale + rng.between(-1, 1)),
                d.xp + scale * 3);
        // WEEK 5: the factory hands each monster its default behaviour, chosen by DATA.
        m.setStrategy(strategyFor(d.strategy));
        return m;
    }

    /**
     * Ordinary (non-boss) blueprint ids for one theme. This is what a ThemeKit draws from for
     * a normal encounter -- bosses are asked for by name, so they cannot wander into a
     * level-one corridor.
     */
    public List<String> idsForTheme(String theme) {
        List<String> out = new ArrayList<>();
        for (MonsterDef d : blueprints.values()) {
            if (d.theme.equals(theme) && !d.boss) out.add(d.id);
        }
        return out;
    }

    /** The boss blueprint for a theme, or null if that theme has none. */
    public String bossIdForTheme(String theme) {
        for (MonsterDef d : blueprints.values()) {
            if (d.theme.equals(theme) && d.boss) return d.id;
        }
        return null;
    }

    public boolean has(String id)  { return blueprints.containsKey(id); }
    public int blueprintCount()    { return blueprints.size(); }

    /**
     * WEEK 5 -- maps a strategy NAME from the data file to a strategy OBJECT.
     *
     * Adding a fifth behaviour is a new class plus one line here plus a data change. Notice
     * that neither Combat nor Monster nor any ThemeKit is involved.
     */
    public static CombatStrategy strategyFor(String name) {
        return switch (name == null ? "" : name.toLowerCase()) {
            case "ranged"   -> new RangedStrategy();
            case "skittish" -> new SkittishStrategy();
            case "healer"   -> new HealerStrategy();
            default         -> new AggressiveStrategy();
        };
    }

    // --- tiny helpers so the JSON reading above stays readable ---

    private static String str(Object o, String fallback) {
        return o instanceof String ? (String) o : fallback;
    }

    private static int num(Object o, int fallback) {
        return o instanceof Number ? ((Number) o).intValue() : fallback;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\RoomPopulator.java
```java
package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.items.Chest;

import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD.
 *
 * populate() is FIXED and final. createEncounter() is DEFERRED to the subclass.
 *
 * That deferral is the pattern. This class decides the ORDER of operations -- flavour, then
 * monsters, then a chest -- and never learns which Monster objects it got back or how many.
 * A subclass decides.
 *
 * Do not confuse this with the ABSTRACT FACTORY next door:
 *   Factory Method   -> one product, subclass decides WHICH ONE.
 *   Abstract Factory -> a family of products, kept mutually consistent.
 *
 * A RoomPopulator HAS-A ThemeKit, so the two patterns compose: the subclass decides how many
 * monsters and whether there is a chest; the kit decides what KIND of monster and loot.
 */
public abstract class RoomPopulator {

    protected final ThemeKit theme;

    protected RoomPopulator(ThemeKit theme) { this.theme = theme; }

    /** THE TEMPLATE. Never override -- the order is the invariant this class protects. */
    public final void populate(Room room, int depth) {
        room.setFlavor(theme.createRoomFlavor());

        for (Monster m : createEncounter(depth)) {
            room.addMonster(m);
        }

        Chest chest = createChest(depth);
        if (chest != null && !chest.isEmpty()) {
            room.setChest(chest);
        }
    }

    /** THE FACTORY METHOD. Abstract: every subclass MUST answer this. */
    protected abstract List<Monster> createEncounter(int depth);

    /** A HOOK. Subclasses MAY override; most do not. Default: no chest. */
    protected Chest createChest(int depth) { return null; }

    /** For the sprint review, so a level can report what kind of rooms it produced. */
    public abstract String kind();
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\StandardRoomPopulator.java
```java
package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;

import java.util.ArrayList;
import java.util.List;

/** WEEK 4 -- FACTORY METHOD. Zero to N ordinary inhabitants, no chest. */
public class StandardRoomPopulator extends RoomPopulator {

    public StandardRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "standard"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        int max = GameConfig.getInstance().getInt("maxMonstersPerRoom");
        int count = RandomSource.getInstance().nextInt(max + 1);
        for (int i = 0; i < count; i++) {
            out.add(theme.createMonster(depth));
        }
        return out;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\ThemeKit.java
```java
package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.items.Item;

/**
 * WEEK 4 -- ABSTRACT FACTORY.
 *
 * A FAMILY of matched products. The constraint that makes this a different pattern from
 * Factory Method is this: a CryptThemeKit can never hand back a Forge monster or Forge loot.
 * The kit guarantees the set goes together.
 *
 * Factory Method varies ONE product and defers the decision to a subclass.
 * Abstract Factory varies a WHOLE FAMILY and keeps the family consistent.
 *
 * The payoff to measure for yourself in Lab Part D: adding a fourth theme is ONE new class
 * and ONE registry line. GameWorld does not change. Neither does RoomPopulator. That is the
 * Open/Closed Principle stated as a number instead of a slogan.
 */
public interface ThemeKit {

    String themeName();

    /** An ordinary inhabitant of this kind of place. */
    Monster createMonster(int depth);

    /** The thing at the bottom of this kind of place. */
    Monster createBoss(int depth);

    /** Loot that belongs in this kind of place. */
    Item createLoot(int depth);

    /** Prose that sounds like this kind of place. */
    String createRoomFlavor();
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\ThemeRegistry.java
```java
package dungeonforge.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- the one place that knows which themes exist.
 *
 * This class is the reason "add a fourth theme" costs one class and one line. Every other
 * class in the game asks for "the theme for depth N" and never names a concrete kit.
 *
 * Lab Part D asks you to add a fourth theme and count the files you had to change. This file
 * should be one of exactly two.
 */
public final class ThemeRegistry {

    private final List<ThemeKit> kits = new ArrayList<>();

    public ThemeRegistry(MonsterFactory factory) {
        kits.add(new CryptThemeKit(factory));
        kits.add(new ForgeThemeKit(factory));
        kits.add(new FrostThemeKit(factory));
        kits.add(new WinterfellThemeKit(factory));
        // Part D: add your fourth kit here. One line.
    }

    /** Levels cycle through the registered themes. */
    public ThemeKit forDepth(int depth) {
        return kits.get((depth - 1) % kits.size());
    }

    public int size() { return kits.size(); }

    public List<String> themeNames() {
        List<String> out = new ArrayList<>();
        for (ThemeKit k : kits) out.add(k.themeName());
        return out;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\TreasureRoomPopulator.java
```java
package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.Chest;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD, and the subclass that uses the HOOK.
 * One guard, and a chest worth guarding.
 */
public class TreasureRoomPopulator extends RoomPopulator {

    public TreasureRoomPopulator(ThemeKit theme) { super(theme); }

    @Override public String kind() { return "treasure"; }

    @Override
    protected List<Monster> createEncounter(int depth) {
        List<Monster> out = new ArrayList<>();
        out.add(theme.createMonster(depth));
        return out;
    }

    /** HOOK OVERRIDE: two or three pieces of theme-appropriate loot. */
    @Override
    protected Chest createChest(int depth) {
        Chest chest = new Chest("Iron-bound Chest");
        int loot = RandomSource.getInstance().between(2, 3);
        for (int i = 0; i < loot; i++) {
            chest.add(theme.createLoot(depth));
        }
        return chest;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\factory\WinterfellThemeKit.java
```java
package dungeonforge.factory;

import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;
import dungeonforge.items.*;

public class WinterfellThemeKit implements ThemeKit {
    private final MonsterFactory factory;

    private static final String[] FLAVORS = {
            "The cold is biting at your nose. Direwolves glare from the forest edge.",
            "In the distance you see a pyre of smoke rising beyond the wall.",
            "The blue eyes of a White Walker peers into your soul like a dagger.",
            "A Direwolf chews on a bloody bone in the corner of the pub. The locals glare at you, the newcomer."
    };

    public WinterfellThemeKit(MonsterFactory factory) {
        this.factory = factory;
    }

    @Override
    public String themeName() {
        return "Winterfell";
    }

    @Override
    public Monster createMonster(int depth) {
        String id = RandomSource.getInstance().pick(factory.idsForTheme("winterfell"));
        return factory.create(id, depth);
    }

    @Override
    public Monster createBoss(int depth) {
        return factory.create("night_king", depth);
    }

    @Override
    public Item createLoot(int depth) {
        switch(RandomSource.getInstance().nextInt(4)) {
            case 0: return new Weapon("Valerian Steel GreatSword", 6.0, 80 + depth*10, 6 + depth);
            case 1: return new Armor("Direwolf pup", 2.0, 45 + depth*8, 5 + depth);
            case 2: return new Potion("Ice Magic", 0.4, 15, 10 + depth * 3);
            default: return new Treasure("Dragon Eggs", 0.5, 60 + depth*15);
        }
    }

    @Override
    public String createRoomFlavor() {
        return RandomSource.getInstance().pick(FLAVORS);
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Armor.java
```java
package dungeonforge.items;

/** WEEK 4 -- armour. */
public class Armor implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int defense;

    public Armor(String name, double weight, int value, int defense) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.defense = defense;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getDefense()             { return defense; }

    @Override
    public String describe() {
        return name + " [def " + defense + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Chest.java
```java
package dungeonforge.items;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- a container of loot, placed by a TreasureRoomPopulator or BossRoomPopulator.
 *
 * Note it is NOT an Item. In Week 13 it becomes part of a COMPOSITE and that changes, but
 * building for that now would be guessing at a design you have not been taught yet.
 */
public class Chest {

    private final String name;
    private final List<Item> contents = new ArrayList<>();

    public Chest(String name) { this.name = name; }

    public String getName()        { return name; }
    public List<Item> getContents() { return contents; }
    public void add(Item item)     { contents.add(item); }
    public boolean isEmpty()       { return contents.isEmpty(); }

    public int totalValue() {
        int sum = 0;
        for (Item i : contents) sum += i.getValue();
        return sum;
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Hourglass.java
```java
package dungeonforge.items;

/**
 * WEEK 7 -- the item that only exists because of the COMMAND pattern.
 *
 * The Chronomancer's Hourglass rewinds the last turn. There is no way to write this feature
 * against a game whose actions are if-branches, because an if-branch leaves nothing behind to
 * reverse. It took one small class and three lines in UseItemCommand.
 *
 * When someone asks whether a pattern was worth the extra classes, this is the answer:
 * it is not that the code got tidier, it is that a feature became possible.
 *
 * KNOWN LIMITATION, and a good extension exercise: using the hourglass spends a charge, and
 * that spend is not itself undoable. Rewind twice in a row and you cannot get the charges
 * back. Fixing it properly means the hourglass needs its own memento -- which is the same
 * lesson TurnSnapshot teaches, one level down.
 */
public class Hourglass implements Item {

    private int charges;

    public Hourglass(int charges) { this.charges = charges; }

    public boolean hasCharges() { return charges > 0; }
    public int getCharges()     { return charges; }
    public void spendCharge()   { if (charges > 0) charges--; }

    @Override public String getName()   { return "Chronomancer's Hourglass"; }
    @Override public double getWeight() { return 0.5; }
    @Override public int getValue()     { return 250; }

    @Override
    public String describe() {
        return getName() + " [" + charges + " charges] (rewinds one turn)";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Item.java
```java
package dungeonforge.items;

/**
 * WEEK 4 -- introduced so that a ThemeKit has loot to hand back.
 *
 * Deliberately minimal. Week 9 will wrap these in DECORATORS to add enchantments, and
 * Week 13 will turn this into a COMPOSITE so bags can hold bags. Do not over-build it now.
 */
public interface Item {
    String getName();
    double getWeight();
    int getValue();

    default String describe() {
        return getName() + " (" + String.format("%.1f", getWeight()) + "kg, " + getValue() + "g)";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Potion.java
```java
package dungeonforge.items;

/** WEEK 4 -- a consumable. */
public class Potion implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int healAmount;

    public Potion(String name, double weight, int value, int healAmount) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.healAmount = healAmount;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getHealAmount()          { return healAmount; }

    @Override
    public String describe() {
        return name + " [heals " + healAmount + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Treasure.java
```java
package dungeonforge.items;

/** WEEK 4 -- pure value, no combat effect. */
public class Treasure implements Item {

    private final String name;
    private final double weight;
    private final int value;

    public Treasure(String name, double weight, int value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\items\Weapon.java
```java
package dungeonforge.items;

/** WEEK 4 -- a weapon. Week 9 will decorate these with enchantments. */
public class Weapon implements Item {

    private final String name;
    private final double weight;
    private final int value;
    private final int damage;

    public Weapon(String name, double weight, int value, int damage) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.damage = damage;
    }

    @Override public String getName()   { return name; }
    @Override public double getWeight() { return weight; }
    @Override public int getValue()     { return value; }
    public int getDamage()              { return damage; }

    @Override
    public String describe() {
        return name + " [dmg " + damage + "] (" + String.format("%.1f", weight) + "kg, " + value + "g)";
    }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\Main.java
```java
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

    public static final String VERSION = "0.7.0";

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
            System.out.print("\n[" + ctx.getState().name() + " | HP " + ctx.getPlayer().getHp() + "/"
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

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\CombatState.java
```java
package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class CombatState implements GameState {
    private final static List<String> VERBS = List.of(
            "attack", "look", "use",  "inventory", "status", "history", "undo", "help", "quit" );


    @Override
    public void onEnter(GameContext ctx) {
        int count = ctx.getCurrentRoom().getMonsters().size();
        ctx.getBus().message("Combat! " + count + " hostile(s). Try 'attack'.");
    }

    @Override
    public void onExit(GameContext ctx) {
        ctx.getBus().message("The room falls quiet.");
    }

    @Override public List<String> availableCommands() { return VERBS; }
    @Override public String name() { return "Combat"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\DeadState.java
```java
package dungeonforge.states;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class DeadState implements MonsterState {
    @Override
    public boolean canAct() { return false; }

    @Override
    public MonsterState next(Monster self, Player player) { return this; }

    @Override
    public String label() { return "dead"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\EnragedState.java
```java
package dungeonforge.states;

import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class EnragedState implements MonsterState {

    public static final double DAMAGE_BONUS = 1.5;

    @Override
    public void onEnter(Monster self) {
        self.setStrategy(new AggressiveStrategy());
    }

    @Override
    public boolean canAct() { return true; }

    @Override
    public double damageMultiplier() { return DAMAGE_BONUS; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();

        double flee = GameConfig.getInstance().getDouble("fleeThreshold");
        if (self.hpFraction() < flee) return new FleeingState();

        return this;
    }

    @Override
    public String label() { return "enraged"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\ExploringState.java
```java
package dungeonforge.states;

import java.util.List;

public class ExploringState implements GameState {
    private final static List<String> VERBS = List.of(
            "north", "south", "down", "look", "take", "loot", "drop", "use",
            "inventory", "pack", "status", "quests", "history", "undo", "help", "quit" );

    @Override
    public List<String> availableCommands() { return VERBS; }

    @Override
    public String name() { return "Exploring"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\FleeingState.java
```java
package dungeonforge.states;

import dungeonforge.behavior.AggressiveStrategy;
import dungeonforge.behavior.SkittishStrategy;
import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class FleeingState implements MonsterState {

    public static final double DAMAGE_DEFICIT = 0.5;

    @Override
    public void onEnter(Monster self) {
        self.setStrategy(new SkittishStrategy());
    }

    @Override
    public boolean canAct() { return true; }

    @Override
    public double damageMultiplier() { return DAMAGE_DEFICIT; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();
        return this;
    }

    @Override
    public String label() { return "fleeing"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\GameOverState.java
```java
package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class GameOverState implements GameState {
    private final static List<String> VERBS = List.of(
            "quests", "status", "history", "quit" );


    @Override
    public void onEnter(GameContext ctx) {
        int count = ctx.getCurrentRoom().getMonsters().size();
        ctx.getBus().message(
                ctx.getPlayer().isAlive()
                        ? "=== YOU leave the dungeon behind === 'quests' for your record, or 'quit'."
                        : "=== YOU DIED === 'quests' to see how far you got, or 'quit'."
        );
    }

    @Override public List<String> availableCommands() { return VERBS; }
    @Override public String name() { return "Game Over"; }
}


```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\GameState.java
```java
package dungeonforge.states;

import dungeonforge.commands.Command;
import dungeonforge.commands.GameContext;

import java.util.List;

public interface GameState {
    default void onEnter(GameContext ctx) {}
    default void onExit(GameContext ctx) {}
    List<String> availableCommands();
    String name();
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\HuntingState.java
```java
package dungeonforge.states;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class HuntingState implements MonsterState {
    @Override
    public boolean canAct() { return true; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();

        double flee = GameConfig.getInstance().getDouble("fleeThreshold");
        double enrage = GameConfig.getInstance().getDouble("enrageThreshold");

        if (self.hpFraction() < flee) return new FleeingState();
        if (self.hpFraction() < enrage) return new EnragedState();

        return this;
    }

    @Override
    public String label() { return "hunting"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\IdleState.java
```java
package dungeonforge.states;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public class IdleState implements MonsterState {
    @Override
    public boolean canAct() { return false; }

    @Override
    public MonsterState next(Monster self, Player player) {
        if(!self.isAlive()) return new DeadState();
        return new HuntingState();
    }

    @Override
    public String label() { return "idle"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\InventoryState.java
```java
package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class InventoryState implements GameState {
    private final static List<String> VERBS = List.of(
            "use",  "inventory", "status", "back", "drop", "help", "quit" );


    @Override
    public void onEnter(GameContext ctx) {
        ctx.getBus().message(" ---- Your pack --- ('back' to close)");
    }

    @Override public List<String> availableCommands() { return VERBS; }
    @Override public String name() { return "Pack"; }
}

```



File: C:\Users\lynet\IdeaProjects\dungeonforge\src\main\java\dungeonforge\states\MonsterState.java
```java
package dungeonforge.states;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;

public interface MonsterState {
    default void onEnter(Monster self) {}
    boolean canAct();
    default double damageMultiplier() {return 1.0; }
    MonsterState next(Monster self, Player player);
    String label();
}

```

