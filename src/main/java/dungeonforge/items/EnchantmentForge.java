package dungeonforge.items;

/**
 * WEEK 9 STARTER -- THE PROBLEM, STATED AS A METHOD.
 *
 * ============================ READ THIS BEFORE PART A ============================
 * Seven subclasses is bad. Having to CHOOSE between seven subclasses is worse, and this
 * class is where that cost lands.
 *
 * To enchant a weapon you must answer: "what is it already, and what does it become?" With
 * a subclass per combination, that question has no general answer -- only a table. Below is
 * that table, written out by hand, and it is already incomplete.
 *
 * Three things are wrong here, and you should be able to name all three in Part A:
 *
 *   1. It is a nested ladder of type tests. Every new enchantment multiplies its size.
 *   2. It CANNOT express some perfectly reasonable weapons. Flaming twice? There is no class
 *      for that, so the method refuses.
 *   3. It returns a NEW OBJECT. The old weapon is a different object, so anything holding a
 *      reference to it -- the equipped slot, a quest, a chest -- now points at the wrong
 *      thing. Try `equip` then `enchant` and watch your damage not change.
 *
 * Item (3) is not a bug in this method. It is a bug in the whole approach, and no amount of
 * care here will fix it. Hold that thought until Part C.
 * =================================================================================
 */
public final class EnchantmentForge {

    private EnchantmentForge() { }

    public static final String[] KNOWN = { "flaming", "frost", "vampiric" };

    public static boolean isKnown(String enchantment) {
        for (String k : KNOWN) if (k.equalsIgnoreCase(enchantment)) return true;
        return false;
    }

    /**
     * Returns a NEW weapon with the enchantment added, or null if no class exists for that
     * combination. Read that sentence again: the combination has to already have a class.
     */
    public static Weapon enchant(Weapon w, String enchantment) {
        String e = enchantment == null ? "" : enchantment.toLowerCase().trim();
        String n = w.getName();
        double kg = w.getWeight();
        int v = baseValue(w);
        int d = baseDamage(w);

        // ---- the table. 3 enchantments x 8 possible starting classes. It gets worse. ----
        if (e.equals("flaming")) {
            if (w instanceof FlamingWeapon)              return null;   // no class for double fire
            if (w instanceof FlamingFrostWeapon)         return null;
            if (w instanceof FlamingVampiricWeapon)      return null;
            if (w instanceof FlamingFrostVampiricWeapon) return null;
            if (w instanceof FrostVampiricWeapon)        return new FlamingFrostVampiricWeapon(n, kg, v, d);
            if (w instanceof FrostWeapon)                return new FlamingFrostWeapon(n, kg, v, d);
            if (w instanceof VampiricWeapon)             return new FlamingVampiricWeapon(n, kg, v, d);
            return new FlamingWeapon(n, kg, v, d);
        }
        if (e.equals("frost")) {
            if (w instanceof FrostWeapon)                return null;
            if (w instanceof FlamingFrostWeapon)         return null;
            if (w instanceof FrostVampiricWeapon)        return null;
            if (w instanceof FlamingFrostVampiricWeapon) return null;
            if (w instanceof FlamingVampiricWeapon)      return new FlamingFrostVampiricWeapon(n, kg, v, d);
            if (w instanceof FlamingWeapon)              return new FlamingFrostWeapon(n, kg, v, d);
            if (w instanceof VampiricWeapon)             return new FrostVampiricWeapon(n, kg, v, d);
            return new FrostWeapon(n, kg, v, d);
        }
        if (e.equals("vampiric")) {
            if (w instanceof VampiricWeapon)             return null;
            if (w instanceof FlamingVampiricWeapon)      return null;
            if (w instanceof FrostVampiricWeapon)        return null;
            if (w instanceof FlamingFrostVampiricWeapon) return null;
            if (w instanceof FlamingFrostWeapon)         return new FlamingFrostVampiricWeapon(n, kg, v, d);
            if (w instanceof FlamingWeapon)              return new FlamingVampiricWeapon(n, kg, v, d);
            if (w instanceof FrostWeapon)                return new FrostVampiricWeapon(n, kg, v, d);
            return new VampiricWeapon(n, kg, v, d);
        }
        return null;
    }

    /**
     * And here is the other tax. Every subclass overrode getDamage() and getValue() to ADD
     * its bonus, so to build the next class up we must first SUBTRACT every bonus already
     * applied and recover the original numbers. Two more hand-written tables.
     */
    private static int baseDamage(Weapon w) {
        if (w instanceof FlamingFrostVampiricWeapon) return w.getDamage() - 4 - 3;
        if (w instanceof FlamingFrostWeapon)         return w.getDamage() - 4 - 3;
        if (w instanceof FlamingVampiricWeapon)      return w.getDamage() - 4;
        if (w instanceof FrostVampiricWeapon)        return w.getDamage() - 3;
        if (w instanceof FlamingWeapon)              return w.getDamage() - 4;
        if (w instanceof FrostWeapon)                return w.getDamage() - 3;
        return w.getDamage();
    }

    private static int baseValue(Weapon w) {
        if (w instanceof FlamingFrostVampiricWeapon) return w.getValue() - 60 - 45 - 90;
        if (w instanceof FlamingFrostWeapon)         return w.getValue() - 60 - 45;
        if (w instanceof FlamingVampiricWeapon)      return w.getValue() - 60 - 90;
        if (w instanceof FrostVampiricWeapon)        return w.getValue() - 45 - 90;
        if (w instanceof FlamingWeapon)              return w.getValue() - 60;
        if (w instanceof FrostWeapon)                return w.getValue() - 45;
        if (w instanceof VampiricWeapon)             return w.getValue() - 90;
        return w.getValue();
    }
}
