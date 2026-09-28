package dungeonforge.events;

import java.util.ArrayDeque;
import java.util.Deque;

public class CombatLog implements GameEventListener {


    private final Deque<String> lines = new ArrayDeque<>();
    private final int maxLines;

    public CombatLog(int maxLines) { this.maxLines = maxLines; }

    @Override
    public void onEvent(GameEvent event) {
        String line = format(event);
        if (line == null) return;
        lines.addLast(line);
        while( lines.size() > maxLines ) lines.removeFirst();
    }

    private String format(GameEvent e) {
        return switch (e.getType()) {
            case MESSAGE -> e.getString("text");
            case DAMAGE_DEALT -> "You hit " + e.getString("target") + " for " + e.getInt("amount");
            case DAMAGE_TAKEN -> e.getString("source") + "hits you for " + e.getInt("amount");
            case MONSTER_DIED -> e.getString("name") + " dies." + e.getInt("xp");
            case MONSTER_FLED -> e.getString("name") + " flees into the dark.";
            case MONSTER_HEALED -> e.getString("healer") + " mends " + e.getString("target");
            case STRATEGY_CHANGED -> e.getString("name") + " changes tactics: "
                    + e.getString("from") + " -> " + e.getString("to");
            case ROOM_CLEARED -> e.getString("room") + " is quiet.";
            case LEVEL_ENTERED -> "== level " + e.getString("depth") + " : " + e.getString("theme");
            case PLAYER_DIED -> "You die in the dark.";
            case DELVE_SURVIVED -> "You climb back into daylight!";
            default -> null;
        };
    }
    public Deque<String> getLines() { return lines; }
    public int size() { return lines.size(); }
}
