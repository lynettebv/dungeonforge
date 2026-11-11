package dungeonforge.events;

import dungeonforge.config.GameConfig;
import dungeonforge.core.Player;

public class DamageMeter implements GameEventListener {
    private final EventBus bus;
    private final Player player;
    private final int threshold;
    private boolean haveWarned;

    public DamageMeter(EventBus bus, Player player, double threshold) {
        this.bus = bus;
        this.player = player;
        this.threshold = (int)Math.round(threshold * GameConfig.getInstance().getDouble("playerStartingHp"));
        this.haveWarned = false;
    }

    @Override
    public void onEvent(GameEvent event) {
        if (haveWarned) return;
        if (event.getType().equals(EventType.DAMAGE_TAKEN)) {
            if (player.getHp() <= threshold) {
                bus.message(String.format(
                        "~~~ DANGER %s ROBINSON: You're dangerously close to death!!!\n      HP: %d, WARN: %d ~~~",
                        player.getName(), player.getHp(), threshold));
                haveWarned = true;
            }
        }
    }
}
