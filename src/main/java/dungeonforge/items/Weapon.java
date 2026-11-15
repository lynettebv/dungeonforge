package dungeonforge.items;

/**
 *
 * Two changes were needed to make the subclasses below possible, and both are dominoes:
 * the fields went from private to protected, and getDamage()/getValue()/describe() stopped
 * being things only this class decides.
 */
public interface Weapon extends Item {

    int getDamage();

    /**
     * WEEK 9 STARTER -- hit points restored to the wielder when this weapon lands a blow.
     * Zero for an ordinary weapon. Combat calls this after every player strike.
     */
    default int lifesteal() { return 0; }

    default Weapon peel() { return this; }

}
