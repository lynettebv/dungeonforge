package dungeonforge.events;

import jdk.jfr.Event;

import java.util.ArrayList;
import java.util.List;

public class QuestTracker implements GameEventListener{

    private final List<Quest> quests = new ArrayList<>();
    private final EventBus bus;

    public QuestTracker(EventBus bus) {
        this.bus = bus;
        quests.add(new Quest( "q1", "Slay 5 creatures of the dungeon", EventType.MONSTER_DIED, 5));
        quests.add(new Quest( "q2", "Drive off 2 enemies", EventType.MONSTER_FLED, 2));
        quests.add(new Quest( "q3", "Clear 6 rooms", EventType.ROOM_CLEARED, 6));
    }

    @Override
    public void onEvent(GameEvent event) {
        for (Quest q : quests) {
            if(q.getTrigger() != event.getType()) continue;
            if(q.advance()) {
                bus.message("QUEST COMPLETE: " + q.getDescription());
            }
        }
    }

    public List<Quest> getQuests() { return quests; }

    public int completedCount() {
        int n = 0;
        for (Quest q : quests) if(q.isComplete()) n++;
        return n;
    }
}
