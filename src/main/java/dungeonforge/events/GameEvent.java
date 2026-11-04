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
