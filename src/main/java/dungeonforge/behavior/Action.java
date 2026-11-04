package dungeonforge.behavior;

import dungeonforge.core.Entity;

/**
 * WEEK 5 -- what a STRATEGY returns.
 *
 * Note that a strategy returns a DECISION, it does not perform one. Deciding and doing are
 * separate jobs: the strategy knows what this monster wants to do, and Combat knows how to
 * make it happen. Keeping those apart is what lets you unit-test a strategy without a
 * running game.
 */
public class Action {

    public enum Type { ATTACK, RANGED_ATTACK, FLEE, HEAL_ALLY, WAIT }

    private final Type type;
    private final Entity target;
    private final String flavor;

    public Action(Type type, Entity target, String flavor) {
        this.type = type;
        this.target = target;
        this.flavor = flavor;
    }

    public static Action wait(String flavor) { return new Action(Type.WAIT, null, flavor); }

    public Type getType()     { return type; }
    public Entity getTarget() { return target; }
    public String getFlavor() { return flavor; }
}
