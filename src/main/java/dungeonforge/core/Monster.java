package dungeonforge.core;

import java.util.Random;

/**
 * WEEK 1 -- a monster.
 *
 * TODO(week 3, US-1.2): this class owns its own Random. So does Room. So does GameWorld.
 * Three independent sources of randomness means the same seed can never reproduce the same
 * dungeon, which means a bug you hit once may never be reproducible. Count how many separate
 * Random instances exist in this project before you start.
 */
public class Monster extends Entity {

    /** Randomness source #1 of 3. Nobody can seed this. */
    private static final Random RNG = new Random();

    private final String species;
    private final int xpReward;

    public Monster(String species, int baseHp, int baseAttack, int xpReward) {
        // A little stat variance so no two monsters are identical.
        super(species,
              baseHp + RNG.nextInt(5) - 2,
              baseAttack + RNG.nextInt(3) - 1,
              0);
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
