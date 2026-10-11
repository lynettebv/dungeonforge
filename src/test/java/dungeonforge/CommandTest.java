package dungeonforge;

import dungeonforge.commands.Command;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;
import dungeonforge.commands.NoCommand;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.EventBus;
import dungeonforge.events.QuestTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 7 -- Sprint 5. The Command pattern. */
class CommandTest {

    private GameContext ctx;
    private CommandParser parser;
    private Player player;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();

        player = new Player("Tester");
        GameWorld world = new GameWorld(player);
        EventBus bus = new EventBus();
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        ctx = new GameContext(world, player, bus, new Combat(bus));
        parser = new CommandParser(quests, achievements);
    }

    private void run(String input) {
        Command c = parser.parse(input, ctx);
        c.execute();
    }

    // ---------- US-5.1: every action is an object ----------

    @Test
    void theParserAlwaysReturnsACommandNeverNull() {
        assertNotNull(parser.parse("north", ctx));
        assertNotNull(parser.parse("flibbertigibbet", ctx));
        assertNotNull(parser.parse("", ctx));
    }

    @Test
    void anUnknownVerbYieldsANullObjectRatherThanAnException() {
        Command c = parser.parse("flibbertigibbet", ctx);
        assertInstanceOf(NoCommand.class, c);
        assertDoesNotThrow(c::execute);
        assertFalse(c.isUndoable());
    }

    @Test
    void aliasesResolveToTheSameCommandType() {
        assertEquals(parser.parse("north", ctx).getClass(), parser.parse("n", ctx).getClass());
        assertEquals(parser.parse("attack", ctx).getClass(), parser.parse("kill", ctx).getClass());
        assertEquals(parser.parse("inventory", ctx).getClass(), parser.parse("i", ctx).getClass());
    }

    @Test
    void movingChangesTheCurrentRoom() {
        Room start = ctx.getCurrentRoom();
        run("north");
        assertNotSame(start, ctx.getCurrentRoom());
    }

    @Test
    void readOnlyCommandsAreNeverUndoable() {
        assertFalse(parser.parse("look", ctx).isUndoable());
        assertFalse(parser.parse("inventory", ctx).isUndoable());
        assertFalse(parser.parse("help", ctx).isUndoable());
    }

    // ---------- helpers ----------

    private Room findRoomWithLoot() {
        for (var level : ctx.getWorld().getLevels()) {
            for (Room r : level.getRooms()) {
                if (r.getChest() != null && r.getChest().getContents().size() >= 2) return r;
            }
        }
        return null;
    }

    private void assumeRoom(Room r) {
        assertNotNull(r, "the seeded world should contain at least one stocked chest");
    }
}
