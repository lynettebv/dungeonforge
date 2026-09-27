package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.core.Room;
import dungeonforge.items.Chest;

import java.util.List;

/**
 * WEEK 4 -- FACTORY METHOD.
 *
 * populate() is FIXED and final. createEncounter() is DEFERRED to the subclass.
 *
 * That deferral is the pattern. This class decides the ORDER of operations -- flavour, then
 * monsters, then a chest -- and never learns which Monster objects it got back or how many.
 * A subclass decides.
 *
 * Do not confuse this with the ABSTRACT FACTORY next door:
 *   Factory Method   -> one product, subclass decides WHICH ONE.
 *   Abstract Factory -> a family of products, kept mutually consistent.
 *
 * A RoomPopulator HAS-A ThemeKit, so the two patterns compose: the subclass decides how many
 * monsters and whether there is a chest; the kit decides what KIND of monster and loot.
 */
public abstract class RoomPopulator {

    protected final ThemeKit theme;

    protected RoomPopulator(ThemeKit theme) { this.theme = theme; }

    /** THE TEMPLATE. Never override -- the order is the invariant this class protects. */
    public final void populate(Room room, int depth) {
        room.setFlavor(theme.createRoomFlavor());

        for (Monster m : createEncounter(depth)) {
            room.addMonster(m);
        }

        Chest chest = createChest(depth);
        if (chest != null && !chest.isEmpty()) {
            room.setChest(chest);
        }
    }

    /** THE FACTORY METHOD. Abstract: every subclass MUST answer this. */
    protected abstract List<Monster> createEncounter(int depth);

    /** A HOOK. Subclasses MAY override; most do not. Default: no chest. */
    protected Chest createChest(int depth) { return null; }

    /** For the sprint review, so a level can report what kind of rooms it produced. */
    public abstract String kind();
}
