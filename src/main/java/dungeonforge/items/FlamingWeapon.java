package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that burns. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FlamingWeapon extends WeaponDecorator {

    private static final int FIRE_BONUS = 4;

    public FlamingWeapon(Weapon wrapped) { super(wrapped); }

    @Override
    protected String prefix() { return "Flaming"; }

    @Override
    protected int valueBonus() { return 60; }

    @Override
    public int getDamage() {
        return wrapped.getDamage() + FIRE_BONUS;
    }
}
