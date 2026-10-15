package dungeonforge.commands;

import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.events.EventBus;

public class GameContext {
    private final EventBus bus;

    private final GameWorld world;
    private final Player player;
    private final Combat combat;
    private final CommandHistory history;

    private Room currentRoom;
    private boolean running = true;

    public GameContext(GameWorld world, Player player, EventBus bus, Combat combat, CommandHistory history) {
        this.bus = bus;
        this.world = world;
        this.player = player;
        this.combat = combat;
        this.history = history;
        this.currentRoom = world.startingRoom();
    }

    public EventBus getBus() { return bus; }
    public GameWorld getWorld() { return world; }
    public Player getPlayer() { return player; }
    public Combat getCombat() { return combat; }
    public CommandHistory getHistory() { return history; }
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room currentRoom) { this.currentRoom = currentRoom; }
    public boolean isRunning() { return running; }
    public void stop() { this.running = false; }

    public void runMonsterTurns() {
        if (player.isAlive()) {
            combat.monsterTurns(player, currentRoom);
        }
    }
}
