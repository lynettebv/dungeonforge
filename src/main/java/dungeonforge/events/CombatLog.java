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
            case MONSTER_SATE_CHANGED -> e.getString("name") + " is now " + e.getString("to") + ".";
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
