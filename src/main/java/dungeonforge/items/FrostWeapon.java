package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that bites with cold. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class FrostWeapon extends WeaponDecorator {

    private static final int FROST_BONUS = 3;

    public FrostWeapon(Weapon wrapped) { super(wrapped); }

    @Override
    protected String prefix() { return "Frost"; }

    @Override
    protected int valueBonus() { return 45; }

    @Override
    public int getDamage() { return wrapped.getDamage() + FROST_BONUS; }
}
