package dungeonforge.events;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private final List<GameEventListener> listeners = new ArrayList<>();

    public void subscribe(GameEventListener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(GameEvent event) {
        for(GameEventListener listener : new ArrayList<>(this.listeners)) {
            listener.onEvent(event);
        }
    }

    public void message(String text) { publish(GameEvent.message(text)); }

    public int listenerCount() { return listeners.size(); }
}
