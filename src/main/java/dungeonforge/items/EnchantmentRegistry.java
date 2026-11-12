package dungeonforge.items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class EnchantmentRegistry {
    private static final Map<String, UnaryOperator<Weapon>> SPELLS = new LinkedHashMap<>();

    private EnchantmentRegistry() {}

    static {
        SPELLS.put("flaming", FlamingWeapon::new);
        SPELLS.put("frost", FrostWeapon::new);
        SPELLS.put("vampiric", VampiricWeapon::new);
        SPELLS.put("keen", KeenWeapon::new);
    }

    public static boolean isKnown(String enchantment) {
        return enchantment != null && SPELLS.containsKey(enchantment);
    }

    public static List<String> known() { return new ArrayList<>(SPELLS.keySet()); }

    public static Weapon apply(String enchantment, Weapon weapon) {
        UnaryOperator<Weapon> spell = SPELLS.get(enchantment);
        if (spell == null) { throw new IllegalArgumentException("No such enchantment: " + enchantment); }
        return spell.apply(weapon);
    }
}
