package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.items.Item;

/**
 * WEEK 4 -- ABSTRACT FACTORY.
 *
 * A FAMILY of matched products. The constraint that makes this a different pattern from
 * Factory Method is this: a CryptThemeKit can never hand back a Forge monster or Forge loot.
 * The kit guarantees the set goes together.
 *
 * Factory Method varies ONE product and defers the decision to a subclass.
 * Abstract Factory varies a WHOLE FAMILY and keeps the family consistent.
 *
 * The payoff to measure for yourself in Lab Part D: adding a fourth theme is ONE new class
 * and ONE registry line. GameWorld does not change. Neither does RoomPopulator. That is the
 * Open/Closed Principle stated as a number instead of a slogan.
 */
public interface ThemeKit {

    String themeName();

    /** An ordinary inhabitant of this kind of place. */
    Monster createMonster(int depth);

    /** The thing at the bottom of this kind of place. */
    Monster createBoss(int depth);

    /** Loot that belongs in this kind of place. */
    Item createLoot(int depth);

    /** Prose that sounds like this kind of place. */
    String createRoomFlavor();
}
