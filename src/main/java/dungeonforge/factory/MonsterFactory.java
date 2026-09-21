package dungeonforge.factory;

import dungeonforge.config.GameConfig;
import dungeonforge.config.Json;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Monster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MonsterFactory {
    private final Map<String, MonsterDef> blueprints = new LinkedHashMap<>();

    public MonsterFactory() {
        loadFrom("monsters.json");
    }

    private void loadFrom(String resourceName) {
        String text = GameConfig.readResource(resourceName);
        if( text == null) {
            System.err.println("[factory] " + resourceName + " not found; no monsters registered");
            return;
        }
        Map<String, Object> root = Json.parseObject(text);
        for(Map.Entry<String, Object> e : root.entrySet()) {
           if(!(e.getValue() instanceof Map)) continue;

           Map<String, Object> m = (Map<String, Object>)e.getValue();

           register(new MonsterDef(
                   e.getKey(),
                   str(m.get("name"), e.getKey()),
                   num(m.get("hp"),10),
                   num(m.get("attack"), 3),
                   num(m.get("xp"), 5),
                   str(m.get("theme"), "crypt"),
                   Boolean.TRUE.equals(m.get("boss"))
           ));
        }
    }

    public void register(MonsterDef def) { blueprints.put(def.getId(), def); }

    public Monster create(String id, int depth) {
        MonsterDef d = blueprints.get(id);
        if(d == null) {
            d = new MonsterDef(id, "Shambling " + id, 10, 3, 5, "crypt");
        }
        int scale = Math.max(0, depth -1);
        RandomSource rng = RandomSource.getInstance();
        return new Monster(d.getName(),
                Math.max(1, d.getHp() + scale + 4 + rng.between(-2, 2)),
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

    public String bossIdFromTheme(String name) {
        for(MonsterDef d : blueprints.values()) {
            if(d.getTheme().equals(name) && d.isBoss()) {
                return d.getId();
            }
        }
        return null;
    }

    public boolean has(String id) {return blueprints.containsKey(id); }
    public int blueprintCount() {return blueprints.size(); }

    private static String str(Object o, String fallback) {
        return o instanceof String ? (String)o : fallback;
    }

    private static int num(Object o, int fallback) {
        return o instanceof Number ? ((Number)o).intValue() : fallback;
    }
}
