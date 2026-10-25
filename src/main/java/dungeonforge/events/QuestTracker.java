package dungeonforge.events;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 5 -- OBSERVER (a concrete observer).
 *
 * Read Combat.java and search it for the word "quest". There are no matches, and there never
 * need to be. Quests are a whole game system that combat has never heard of.
 */
public class QuestTracker implements GameEventListener {

    private final List<Quest> quests = new ArrayList<>();
    private final EventBus bus;

    public QuestTracker(EventBus bus) {
        this.bus = bus;
        quests.add(new Quest("q1", "Slay 5 creatures of the dungeon", EventType.MONSTER_DIED, 5));
        quests.add(new Quest("q2", "Drive off 2 enemies", EventType.MONSTER_FLED, 2));
        quests.add(new Quest("q3", "Clear 6 rooms", EventType.ROOM_CLEARED, 6));
    }

    @Override
    public void onEvent(GameEvent event) {
        for (Quest q : quests) {
            if (q.getTrigger() != event.getType()) continue;
            if (q.advance()) {
                bus.message("QUEST COMPLETE: " + q.getDescription());
            }
        }
    }

    public List<Quest> getQuests() { return quests; }

    public int completedCount() {
        int n = 0;
        for (Quest q : quests) if (q.isComplete()) n++;
        return n;
    }
}
