package dungeonforge.core;

import dungeonforge.behavior.CombatStrategy;
import dungeonforge.config.RandomSource;

/**
 * WEEK 1 -- a monster.
 * WEEK 3 -- stat variance comes from the one seeded RandomSource.
 * WEEK 5 -- a monster HAS-A CombatStrategy. It does not IS-A anything behavioural.
 *
 * That one field is the whole Strategy pattern. Because behaviour is a thing the monster
 * HOLDS, it can be handed a different one mid-fight -- see Combat.checkForTacticsChange().
 * If behaviour were expressed through subclassing, that swap would be impossible: you cannot
 * change an object's class at runtime.
 */
public class Monster extends Entity {

    private final String species;
    private final int xpReward;
    private CombatStrategy strategy;

    /**
     * TODO(week 8, US-6.3): a monster's LIFECYCLE, stored as a string.
     *
     * "idle" -> "hunting" -> "enraged" -> "fleeing" -> "dead"
     *
     * This is exactly how the Gumball Machine starts in Chapter 10 of your textbook: a field
     * holding the current state, and conditionals about that field scattered through every
     * method that cares. Grep Combat.java for getStateName() before you start, and count.
     */
    private String stateName = "idle";

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

    // --- WEEK 5: STRATEGY, held by composition and swappable at runtime ---
    public CombatStrategy getStrategy()              { return strategy; }
    public void setStrategy(CombatStrategy strategy) { this.strategy = strategy; }

    public String getStateName()          { return stateName; }
    public void setStateName(String name) { this.stateName = name; }

    /** How close to death, as a fraction. Strategies read this to decide whether to run. */
    public double hpFraction() { return maxHp == 0 ? 0 : (double) hp / maxHp; }

    @Override
    public String describe() {
        return species + " (" + hp + "/" + maxHp + " HP, ATK " + attackPower
                + ", " + stateName + ")";
    }
}
