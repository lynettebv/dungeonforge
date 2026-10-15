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
