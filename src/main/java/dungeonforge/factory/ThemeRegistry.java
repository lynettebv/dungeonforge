package dungeonforge.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 4 -- the one place that knows which themes exist.
 *
 * This class is the reason "add a fourth theme" costs one class and one line. Every other
 * class in the game asks for "the theme for depth N" and never names a concrete kit.
 *
 * Lab Part D asks you to add a fourth theme and count the files you had to change. This file
 * should be one of exactly two.
 */
public final class ThemeRegistry {

    private final List<ThemeKit> kits = new ArrayList<>();

    public ThemeRegistry(MonsterFactory factory) {
        kits.add(new CryptThemeKit(factory));
        kits.add(new ForgeThemeKit(factory));
        kits.add(new FrostThemeKit(factory));
        kits.add(new WinterfellThemeKit(factory));
    }

    /** Levels cycle through the registered themes. */
    public ThemeKit forDepth(int depth) {
        return kits.get((depth - 1) % kits.size());
    }

    public int size() { return kits.size(); }

    public List<String> themeNames() {
        List<String> out = new ArrayList<>();
        for (ThemeKit k : kits) out.add(k.themeName());
        return out;
    }
}

