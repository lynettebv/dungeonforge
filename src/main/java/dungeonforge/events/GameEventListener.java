package dungeonforge.events;

/**
 * WEEK 5 -- OBSERVER.
 *
 * The entire contract is one method. Implement it, subscribe, and you get told about
 * everything. That is deliberately tiny: the cheaper it is to become a listener, the more
 * likely it is that new features get added as listeners instead of as edits to Combat.
 */
public interface GameEventListener {
    void onEvent(GameEvent event);
}
