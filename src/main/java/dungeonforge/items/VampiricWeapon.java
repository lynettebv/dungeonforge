package dungeonforge.items;

/**
 * WEEK 9 STARTER -- a weapon that drinks. One enchantment.
 *
 * PART OF THE PROBLEM. Do not improve this class. Count it.
 */
public class VampiricWeapon extends WeaponDecorator {

    public VampiricWeapon(Weapon wrapped) { super(wrapped); }

    @Override
    public int lifesteal() { return wrapped.lifesteal() + Math.max(1, wrapped.getDamage() / 4); }

    @Override protected String prefix() { return "Vampiric"; }
    @Override protected int valueBonus() { return 90; }
}
