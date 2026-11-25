package dungeonforge.legacy;

import dungeonforge.factory.MonsterDef;
import dungeonforge.factory.MonsterSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LegacyBestiaryAdapter implements MonsterSource {

    private static final int HP_PER_HIT_DIE = 4;
    private static final int HP_BASE = 2;

    private final LegacyBestiary adaptee;
    private final String name;
    private final List<String> notes = new ArrayList<>();

    public LegacyBestiaryAdapter(LegacyBestiary adaptee, String name) {
        this.adaptee = adaptee;
        this.name = name;
    }

    @Override
    public List<MonsterDef> definitions() {
        List<MonsterDef> out = new ArrayList<>();
        notes.clear();
        adaptee.rewind();
        while (adaptee.advance()) {
            String legacyId = adaptee.field("MOB_ID");
            if (legacyId.isEmpty()) continue;

            int hp = hitPointsFrom(adaptee.field("HIT_DICE"));
            int attack = attackFrom(adaptee.field("DMG"));
            int xp = experienceFor(hp, attack, legacyId);
            String strat = strategyFrom(adaptee.field("AI"), legacyId);

            out.add(new MonsterDef(
                    legacyId.toLowerCase(),
                    adaptee.field("DISPLAY"),
                    hp,
                    attack,
                    xp,
                    themeFrom(adaptee.field("ZONE"), legacyId),
                    "BOSS".equalsIgnoreCase(adaptee.field("TIER")),
                    strat)
            );
        }
        return out;
    }

    private int hitPointsFrom(String hitDice) {
        return Math.max(1, parseInt(hitDice, 1)) * HP_PER_HIT_DIE + HP_BASE;
    }

    private int attackFrom(String expression) {
        String e = expression.toLowerCase().trim(); // normalize table-top dice notation
        int d = e.indexOf('d');
        if (d < 0) return Math.max(1, parseInt(e, 3));
        int plus = e.indexOf('+', d);
        int count = parseInt(e.substring(0, d), 1);
        int sides = parseInt(plus < 0 ? e.substring(d + 1) : e.substring(d + 1, plus), 6);
        int bonus = plus < 0 ? 0 : parseInt(e.substring(plus + 1), 0);
        return Math.max(1, count * ((sides + 1) / 2) + bonus);
    }

    private int experienceFor(int hp, int attack, String legacyId) {
        notes.add(legacyId + ": xp derived (no xp column in the export)");
        return Math.max(1, hp / 2 + attack);
    }

    private String strategyFrom(String ai, String legacyId) {
        switch (ai.trim().toUpperCase()) {
            case "BRAWLER": return "aggressive";
            case "ARCHER": return "ranged";
            case "COWARD": return "skittish";
            case "MEDIC": return "healer";
            default:
                notes.add(legacyId + ": AI '" + ai + "' has no counterpart, using aggressive.");
                return "aggressive";
        }
    }

    private String themeFrom(String zone, String legacyId) {
        switch (zone.trim()) {
            case "1": return "crypt";
            case "2": return "forge";
            case "3": return "frost";
            case "4": return "winterfell";
            default:
                notes.add(legacyId + ": unknown ZONE '" + zone + "', filed under crypt");
                return "crypt";
        }
    }

    @Override
    public String sourceName() {
        return this.name;
    }

    public final List<String> translationNotes() {
        return Collections.unmodifiableList(notes);
    }

    private static int parseInt(String s, int fallback) {
        try {
            return Integer. parseInt(s.trim());
        } catch (RuntimeException e) { return fallback; }
    }
}
