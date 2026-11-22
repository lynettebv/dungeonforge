package dungeonforge.legacy;

import dungeonforge.config.GameConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 12 (PROVIDED -- DO NOT MODIFY THIS FILE.)
 *
 * The previous team's bestiary reader, recovered along with bestiary.csv. Treat it the way you
 * would treat a class from a jar: you can call it, you cannot change it, and nobody is going to
 * fix it for you.
 *
 * ==================== WHY IT IS AWKWARD ====================
 * It is a CURSOR, not a collection. You advance it one record at a time and ask the current
 * record for a field by the legacy column name, as a String. Everything is a String, including
 * the numbers.
 *
 *     LegacyBestiary b = new LegacyBestiary("bestiary.csv");
 *     while (b.advance()) {
 *         String id = b.field("MOB_ID");       // "BARROW_WIGHT"
 *         String hd = b.field("HIT_DICE");     // "4"
 *     }
 *
 * Nothing in DungeonForge can consume that. MonsterFactory.register() wants a MonsterDef:
 * lower-case id, int hp, int attack, int xp, a theme name, a boolean, a strategy name.
 *
 * This is the same shape as the problem Head First solves in Chapter 7 with the
 * Enumeration-to-Iterator adapter (pp. 248-250): an old cursor API and a new collection API,
 * neither of which you may edit.
 * ===========================================================
 *
 * It is not broken. It is just not yours.
 */
public final class LegacyBestiary {

    private final List<Map<String, String>> records = new ArrayList<>();
    private int cursor = -1;

    public LegacyBestiary(String resourceName) {
        String text = GameConfig.readResource(resourceName);
        if (text == null) {
            System.err.println("[legacy] " + resourceName + " not found; bestiary is empty.");
            return;
        }
        List<String> columns = null;
        for (String raw : text.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) {
                // The LAST comment line that contains pipes is the column header. The 0.4
                // exporter wrote it that way and there is no changing it now.
                if (line.contains("|")) {
                    columns = new ArrayList<>();
                    for (String c : line.substring(1).trim().split("\\|")) columns.add(c.trim());
                }
                continue;
            }
            if (columns == null) continue;
            String[] cells = line.split("\\|", -1);
            Map<String, String> record = new LinkedHashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                record.put(columns.get(i), i < cells.length ? cells[i].trim() : "");
            }
            records.add(record);
        }
    }

    /** Moves to the next record. Returns false once there are none left. */
    public boolean advance() {
        if (cursor + 1 >= records.size()) return false;
        cursor++;
        return true;
    }

    /** The current record's value for a legacy column name, or "" if there is no such column. */
    public String field(String column) {
        if (cursor < 0 || cursor >= records.size()) return "";
        return records.get(cursor).getOrDefault(column, "");
    }

    /** Back to before the first record, so the cursor can be walked again. */
    public void rewind() { cursor = -1; }

    public int recordCount() { return records.size(); }
}
