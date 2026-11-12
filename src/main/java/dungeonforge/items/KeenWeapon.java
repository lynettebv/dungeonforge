package dungeonforge.items;

public class KeenWeapon extends WeaponDecorator {

    private static final double EDGE = 1.5;

    public KeenWeapon(Weapon wrapped) {
        super(wrapped);
    }

    @Override
    protected String prefix() { return "Keen"; }

    @Override
    protected int valueBonus() { return 75; }

    @Override
    public int getDamage() {
        return (int)Math.round(wrapped.getDamage() * EDGE); }
}
