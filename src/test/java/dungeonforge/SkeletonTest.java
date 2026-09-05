package dungeonforge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WEEK 2 -- proof that the pipeline works end to end.
 *
 * These tests are trivial ON PURPOSE. Their job is not to verify the game; there is no
 * game yet. Their job is to prove that `mvn test` runs, that CI picks it up, and that a
 * red build blocks your pull request.
 *
 * Try this once: break one assertion deliberately, push it, and watch the PR go red.
 * Learning to read a CI failure now is much cheaper than learning it in Week 9.
 */
class SkeletonTest {

    @Test
    void versionIsSet() {
        assertNotNull(Main.VERSION);
        assertFalse(Main.VERSION.isBlank());
    }

    @Test
    void bannerNamesTheProject() {
        assertTrue(Main.banner().contains("D U N G E O N F O R G E"));
    }

    @Test
    void greetingUsesTheNameGiven() {
        assertTrue(Main.greeting("Aria").startsWith("Welcome, Aria."));
    }

    @Test
    void greetingFallsBackWhenNameIsMissing() {
        assertTrue(Main.greeting(null).contains("Delver"));
        assertTrue(Main.greeting("   ").contains("Delver"));
    }
}
