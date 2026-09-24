package dungeonforge.factory;

import java.util.ArrayList;
import java.util.List;

public class ThemeRegistry {
    private final List<ThemeKit> kits = new ArrayList<>();

    public ThemeRegistry(MonsterFactory factory) {
        kits.add(new CryptThemeKit(factory));
        kits.add(new ForgeThemeKit(factory));
        kits.add(new FrostThemeKit(factory));
    }

    public ThemeKit forDepth(int depth) {
        return kits.get(depth - 1 % kits.size());
    }

    public int size() { return kits.size(); }

    public List<String> themeName() {
        List<String> out = new ArrayList<>();
        for(ThemeKit k : kits) {
            out.add(k.themeName());
        }
        return out;
    }
}
