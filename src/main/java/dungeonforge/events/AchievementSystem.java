package dungeonforge.events;

import java.util.LinkedHashSet;
import java.util.Set;

public class AchievementSystem implements GameEventListener{

    private final Set<String> unlocked = new LinkedHashSet<>();
    private final EventBus bus;

    private int kills;
    private int fled;
    private int deepestLevel;

    public AchievementSystem(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void onEvent(GameEvent event) {
        switch (event.getType()) {
            case MONSTER_DIED -> {
                kills++;
                if(kills == 1) { unlock( "First Blood", "Defeat your first monster"); }
                if(kills == 10) { unlock( "Exterminator", "Defeat 10 monsters"); }
            }
            case MONSTER_FLED -> {
                fled++;
                if(fled == 1) { unlock( "Terrifying", "Make something run away from you"); }
            }
            case STRATEGY_CHANGED -> {
                unlock("Cornered", "Force an enemy to change tactics");
            }
            case LEVEL_ENTERED -> {
                deepestLevel = Math.max(deepestLevel, event.getInt("depth"));
                if (deepestLevel >= 3) unlock("Deep Delver", "Reach dungeon level 3");
            }
            case DELVE_SURVIVED -> {
                unlock("Survivor", "Walk out of the dungeon alive");
            }
            default -> {}
        }
    }
    private void unlock(String name, String description) {
        if( unlocked.add(name) ) {
            bus.message("ACHIEVEMENT: " + name + " -- " + description);
        }
    }

    public Set<String> getUnlocked(){ return unlocked; }
}
