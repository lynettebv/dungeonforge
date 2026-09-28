package dungeonforge.behavior;

import dungeonforge.core.Entity;

public class Action {
    public enum Type {ATTACK, RANGED_ATTACK, FLEE, HEAL_ALLY, WAIT }

    private final Type type;
    private final Entity target;
    private final String flavor;

    public Action(Type type, Entity target, String flavor) {
        this.type = type;
        this.target = target;
        this.flavor = flavor;
    }

    public static Action wait(String flavor) {
        return new Action(Type.WAIT, null, flavor);
    }

    public Type getType() { return type; }
    public Entity getTarget() { return target; }
    public String getFlavor() { return flavor; }

}
