package dungeonforge.behavior;

import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;

/**
 * WEEK 5 -- STRATEGY.
 *
 * "Define a family of algorithms, encapsulate each one, and make them interchangeable.
 *  Strategy lets the algorithm vary independently from clients that use it."
 *
 * ===================== WHY NOT JUST SUBCLASS MONSTER? =====================
 * Because behaviour varies INDEPENDENTLY of species. Any species can be aggressive; any
 * species can be skittish; and the same skeleton needs to switch from one to the other
 * halfway through a fight.
 *
 * Subclassing gives you AggressiveSkeleton, SkittishSkeleton, RangedSkeleton, AggressiveImp,
 * SkittishImp... one class per combination. 15 species x 4 behaviours = 60 classes, and a
 * monster still could not change its mind mid-fight, because you cannot change an object's
 * class at runtime.
 *
 * Composition gives you 15 species + 4 strategies = 19 classes, and a one-line swap.
 * That is the SimUDuck argument, and Part D1 asks you to do the arithmetic yourself.
 * =========================================================================
 *
 * Compare with Week 8's State pattern, which has nearly identical UML and the opposite
 * intent. Strategy answers HOW to fight; State will answer WHEN to fight, flee or rage.
 */
public interface CombatStrategy {

    /** Decide, do not act. Combat performs whatever this returns. */
    Action chooseAction(Monster self, Player target, Room room);

    /** For logs and tests. */
    String name();
}
