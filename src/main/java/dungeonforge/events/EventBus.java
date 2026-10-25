package dungeonforge.events;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- OBSERVER (the Subject).
 *
 * ============================== THE POINT ==============================
 * Combat publishes. It has ZERO references to QuestTracker, AchievementSystem or CombatLog,
 * and it never will. Adding a fourth listener requires no change to any publisher.
 *
 * Part D2 asks you to prove that with `git diff --stat` rather than take my word for it.
 * =======================================================================
 *
 * "Define a one-to-many dependency between objects so that when one object changes state,
 *  all of its dependents are notified and updated automatically."
 *
 * Design principle: strive for LOOSELY COUPLED designs between objects that interact. The
 * only thing the two sides share is GameEvent.
 */
public final class EventBus {

    private final List<GameEventListener> listeners = new ArrayList<>();

    public void subscribe(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(GameEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Iterating a COPY matters. A listener is allowed to unsubscribe itself while being
     * notified, and without the copy that would throw ConcurrentModificationException.
     * This is the bug students hit first, and it is worth hitting once on purpose.
     */
    public void publish(GameEvent event) {
        for (GameEventListener listener : new ArrayList<>(listeners)) {
            listener.onEvent(event);
        }
    }

    public void message(String text) { publish(GameEvent.message(text)); }

    public int listenerCount() { return listeners.size(); }
}
