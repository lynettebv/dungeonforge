package dungeonforge.core;


/**
 * WEEK 1 -- a monster.
 * WEEK 3 (US-1.2) -- its private Random is gone. Stat variance now comes from the one
 * seeded RandomSource, so the same seed always produces the same monster.
 */
public class Monster extends Entity {

    private final String species;
    private final int xpReward;

    /**
     * WEEK 5 correction: stat variance used to be rolled here AND again in MonsterFactory,
     * so every monster was randomised twice and no test could build one with exact hit
     * points. Variance is a CREATION concern, so it now lives only in the factory. A Monster
     * is exactly what it was constructed with.
     */
    public Monster(String species, int maxHp, int attackPower, int xpReward) {
        super(species, maxHp, attackPower, 0);
        this.species = species;
        this.xpReward = xpReward;
    }

    public String getSpecies() { return species; }
    public int getXpReward()   { return xpReward; }

    @Override
    public String describe() {
        return species + " (" + hp + "/" + maxHp + " HP, ATK " + attackPower + ")";
    }
}
