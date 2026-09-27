package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.Json;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 4 -- SIMPLE FACTORY.
 *
 * ============================ SAY THIS OUT LOUD ============================
 * Simple Factory is NOT one of the Gang of Four patterns. It is a useful IDIOM, and Head
 * First is careful to make that distinction before it teaches Factory Method.
 *
 * What it gives you: construction happens in exactly one place, so `new Monster(...)`
 * appears once in the whole codebase instead of scattered through GameWorld.
 *
 * What it does NOT give you: any way for a caller to VARY what gets created without
 * changing this class. That is what Factory Method adds, and it is why the two are
 * different patterns rather than the same one at different sizes.
 * ==========================================================================
 *
 * Note the registry Map rather than an if/else chain over ids. Adding a monster is a line in
 * monsters.json -- no Java changes at all.
 */
public class MonsterFactory {
    private final Map<String, MonsterDef> blueprints = new LinkedHashMap<>();

    public MonsterFactory() {
       loadFrom("monsters.json");
    }

    /** Reads blueprints from a classpath resource. Content is data, not code. */
    private void loadFrom(String resourceName) {
        String text = GameConfig.readResource(resourceName);
        if (text == null) {
            System.err.println("[factory] " + resourceName + " not found; no monsters registered.");
            return;
        }
        Map<String, Object> root = Json.parseObject(text);
        for (Map.Entry<String, Object> e : root.entrySet()) {
            if (!(e.getValue() instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) e.getValue();
            register(new MonsterDef(
                    e.getKey(),
                    str(m.get("name"), e.getKey()),
                    num(m.get("hp"), 10),
                    num(m.get("attack"), 3),
                    num(m.get("xp"), 5),
                    str(m.get("theme"), "crypt"),
                    Boolean.TRUE.equals(m.get("boss"))));
        }
    }

    public void register(MonsterDef def) { blueprints.put(def.getId(), def); }

    /**
     * THE factory method of the Simple Factory idiom -- the single place `new Monster` lives.
     * Stats scale with depth and carry a little seeded variance.
     */
    public Monster create(String id, int depth) {
        MonsterDef d = blueprints.get(id);
        if (d == null) {
            // Unknown ids must not crash the game. AC4 of US-2.1.
            d = new MonsterDef(id, "Shambling " + id, 10, 3, 5, "crypt");
        }
        int scale = Math.max(0, depth - 1);
        RandomSource rng = RandomSource.getInstance();
        return new Monster(d.getName(),
                Math.max(1, d.getHp() + scale * 4 + rng.between(-2, 2)),
                Math.max(1, d.getAttack() + scale + rng.between(-1, 1)),
                d.getXp() + scale * 3);
    }

    public List<String> idsForTheme(String name) {
         List<String> out = new ArrayList<>();
         for(MonsterDef d : blueprints.values()) {
             if(d.getTheme().equals(name) && !d.isBoss()) {
                 out.add(d.getId());
             }
         }
         return out;
    }

    /** The boss blueprint for a theme, or null if that theme has none. */
    public String bossIdFromTheme(String theme) {
        for (MonsterDef d : blueprints.values()) {
            if(d.getTheme().equals(theme) && d.isBoss()) return d.getId();
        }
        return null;
    }

    public boolean has(String id)  { return blueprints.containsKey(id); }
    public int blueprintCount()    { return blueprints.size(); }

    // --- tiny helpers so the JSON reading above stays readable ---

    private static String str(Object o, String fallback) {
        return o instanceof String ? (String) o : fallback;
    }

    private static int num(Object o, int fallback) {
        return o instanceof Number ? ((Number) o).intValue() : fallback;
    }
}
