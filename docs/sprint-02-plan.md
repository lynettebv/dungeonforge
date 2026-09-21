# Sprint 2 Plan — Week 4 — Factory & Abstract Factory — SUPPLIED

**Epic:** E4, Content creation · **Patterns:** Simple Factory (idiom), Factory Method, Abstract Factory

> Still supplied. You write your first sprint plan in Week 6 — two sprints from now.

## Sprint Goal

> A dungeon level is a *place*, not a number: its monsters, its loot and its prose all belong
> to the same theme, and none of them are named anywhere in `GameWorld`.

## Capacity — now based on your own data

This is the first sprint planned against **your measured velocity** rather than a guess.

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 0 (Week 2) | 7 | ____ |
| Sprint 1 (Week 3) | 8 | ____ |

Fill those in from your own calibration tables. If you completed roughly 7–8 points in each
of the first two sprints, **10 points is an honest commitment** and that is what this sprint
asks for. If you completed noticeably fewer, say so in your retro — the fix is to cut scope,
not to work weekends.

## Committed stories

| Issue | Story | Points | Pattern |
|---|---|---|---|
| US-2.1 | Monsters come from data, not from code | 3 | Simple Factory |
| US-2.2 | Each level is a place with a consistent character | 4 | Abstract Factory |
| US-2.3 | Different kinds of room hold different things | 3 | Factory Method |

**Capacity:** ~10 points · **Committed:** 10 points

## Suggested order

1. **US-2.1 first.** Both other stories ask the `MonsterFactory` for monsters, so it must exist.
2. **US-2.2 second.** `RoomPopulator` holds a `ThemeKit`, so kits come before populators.
3. **US-2.3 last.**

That order is not arbitrary — it follows the dependency arrows on this week's UML. When you
write your own plans in Week 6, reading the diagram for ordering is the trick to remember.

## Risks

| Risk | Mitigation |
|---|---|
| Confusing the three factory variants and building one where another belongs | Part D is the clinic that sorts this out. Do it *before* the code if the names blur. |
| Over-generalising — a factory for everything | You need exactly three new abstractions this week. If you are writing a fourth, stop and ask what varies. |
| Bosses leaking into ordinary encounters | The blueprint data has a `boss` flag. Filter the ordinary spawn pool on it. |
| Breaking Week 3's determinism | `FactoryTest` includes a regression test for it. Run it. |

## Definition of Done

`docs/definition-of-done.md` applies unchanged.

---

# ↓ You fill in these at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| US-2.1 | 3 | | |
| US-2.2 | 4 | | |
| US-2.3 | 3 | | |

**Points completed:** ____ · **Running velocity (sprints 0–2):** ____

## Sprint Review — one sentence

> What can the project do now that it could not do last week?


