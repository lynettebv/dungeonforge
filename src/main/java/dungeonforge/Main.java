package dungeonforge;

/**
 * WEEK 2 -- THE WALKING SKELETON.
 *
 * This program does almost nothing, and that is the entire point.
 *
 * A walking skeleton is the smallest thing that exercises your whole pipeline:
 * source -> compile -> test -> CI -> merge -> tag. When this runs green, you know
 * the MACHINE works. Every failure after today is your code, not your setup.
 *
 * From Week 3 this file grows into the real game. Do not delete it.
 */
public final class Main

    /** Bumped every sprint. Week 3 replaces this with the GameConfig singleton. */
    public static final String VERSION = "0.1.0";

    private Main() { }

    public static String banner() {
        return """
                =========================================
                        D U N G E O N F O R G E
                  A Head First Design Patterns project
                =========================================""";
    }

    public static String greeting(String name) {
        if (name == null || name.isBlank()) {
            name = "Delver";
        }
        return "Welcome, " + name + ". The dungeon is not built yet. That starts in Week 3.";
    }

    public static void main(String[] args) {
        System.out.println(banner());
        System.out.println("  version " + VERSION);
        System.out.println();
        System.out.println(greeting(args.length > 0 ? args[0] : null));
    }
}
