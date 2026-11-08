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
        if (state.isTerminal() ) return;

        if (!player.isAlive()) {
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
