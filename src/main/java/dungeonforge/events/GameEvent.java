package dungeonforge.events;

import java.util.LinkedHashMap;
import java.util.Map;

public final class GameEvent {


    private final EventType type;
    private final Map<String, Object> payload = new LinkedHashMap<>();

    public GameEvent(EventType type) {
        this.type = type;
    }

    public static GameEvent of(EventType type, Object... keyValuePairs) {
        GameEvent e = new GameEvent(type);
        for (int i = 0; i + 1 < keyValuePairs.length; i+= 2) {
            e.payload.put(String.valueOf(keyValuePairs[i]), keyValuePairs[i + 1]);
        }
        return e;
    }

    public static GameEvent message(String text) { return of(EventType.MESSAGE, "text", text); }

    public EventType getType() { return type; }
    public Object get(String key) { return this.payload.get(key); }

    public String getString(String key) {
        Object o = payload.get(key);
        return o == null ? "" : String.valueOf(o);
    }

    public int getInt(String key) {
        Object o = payload.get(key);
        return o instanceof Number ? ((Number)o).intValue() : 0;
    }

    @Override
    public String toString() {
        return this.type + payload.toString();
    }
}
