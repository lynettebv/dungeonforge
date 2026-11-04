package dungeonforge.events;

/** WEEK 5 -- plain data for the QuestTracker. Not a pattern; just a countable goal. */
public class Quest {

    private final String id;
    private final String description;
    private final EventType trigger;
    private final int required;
    private int progress;

    public Quest(String id, String description, EventType trigger, int required) {
        this.id = id;
        this.description = description;
        this.trigger = trigger;
        this.required = required;
    }

    public String getId()          { return id; }
    public String getDescription() { return description; }
    public EventType getTrigger()  { return trigger; }
    public int getRequired()       { return required; }
    public int getProgress()       { return progress; }
    public boolean isComplete()    { return progress >= required; }

    /** Returns true on the tick that COMPLETES the quest, so the caller can announce it once. */
    public boolean advance() {
        if (isComplete()) return false;
        progress++;
        return isComplete();
    }

    @Override
    public String toString() {
        return (isComplete() ? "[x] " : "[ ] ") + description
                + " (" + Math.min(progress, required) + "/" + required + ")";
    }
}
