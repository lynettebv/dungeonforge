# Lab 9 — Decorator — Starter

This is your own Week 8 repo, moved forward one week, with this week's problem planted in it.
Everything you built in Weeks 3, 4, 5, 7 and 8 is still here and still passing: **86 tests,
green, before you touch anything.** Run them first.

```bash
mvn test                      # expect 86 passing
mvn -q exec:java
```

---

## Provided for you this week

| Thing | Why |
|---|---|
| `Player.equippedWeapon`, `equip()`, `unequip()`, the `getAttackPower()` override | So a weapon can matter. **This is the feature you costed out in Week 7's evidence sheet** — go and compare your prediction with the real invoice; the note in `Player.java` walks through it. |
| `EquipCommand` + one `register()` + one `alias()` | The wiring half of that same prediction. Four lines. |
| `Combat`'s lifesteal line in `playerStrikes()` | One line, and it will still be one line at the end of the week no matter how many enchantments exist. The damage calculation did not change at all. |
| A **Notched Shortsword** in your starting inventory | So Part 0 works on turn one instead of waiting for a loot drop. |
| `EnchantCommand` | A verb that half works. Read its failure messages. |
| Seven enchanted weapon classes | **This is the problem.** |
| `EnchantmentForge` | And this is the other half of it. |

**Two tidy-ups you will notice in the diff.** `Combat.java` had three `TODO(week 8)` comments
describing an `updateMonsterState()` method and a string state field — neither of which exists
any more, because your State refactor removed them. Comments that outlive their code are worse
than no comments, so they are gone. `GameState.java` also had an unused `import`. Neither is a
design change.

## The problem

Three enchantments — flaming, frost, vampiric — are modelled as one class per **combination**.

```
Weapon
├── FlamingWeapon
├── FrostWeapon
├── VampiricWeapon
├── FlamingFrostWeapon
├── FlamingVampiricWeapon
├── FrostVampiricWeapon
└── FlamingFrostVampiricWeapon
```

Seven classes for three enchantments, because 2³ − 1 = 7. The fire arithmetic appears in **five
files**. `EnchantmentForge` is a 108-line ladder of **34 type tests** with **13 `return null`
paths**, and it exists only to answer *"what is this weapon already, and what class does it
become?"* — plus two more hand-written tables that subtract the bonuses back off so the next
class up can be rebuilt from scratch.

Four things are broken because of it, and Part 0 of `docs/evidence.week9.md` asks you to
reproduce all four:

1. **Enchanting the weapon in your hand does nothing to your damage.** Enchanting builds a
   *different object*; your hand still holds the old one.
2. **You cannot apply the same enchantment twice.** There is no class for it.
3. **You cannot add a fourth enchantment** without writing eight more classes.
4. **Changing the fire bonus means editing five files**, and nothing will tell you if you miss
   one.

Only (2), (3) and (4) are the pattern's fault. **(1) is an ordinary bug that you will fix by
hand, and Decorator will not help you find it.** Keeping those two categories apart is worth
marks in Part D4.

## What you are building

`docs/sprint-07-plan.md` — five stories, acceptance criteria and sub-tasks, all supplied. Note
that the sprint opens with **US-6.7, carried over from Sprint 6**: one point of debt that has
nothing to do with Decorator. Read the `[AC*]` tag on each sub-task; mapping criteria to tasks
is the thing being practised.

```
Weapon (interface)
├── BasicWeapon
└── WeaponDecorator (abstract, HAS-A Weapon)
    ├── FlamingWeapon
    ├── FrostWeapon
    ├── VampiricWeapon
    └── KeenWeapon        (US-7.3 -- the one you add to measure the cost)
```

Six classes for four enchantments, and the seventh enchantment would be the seventh class.
Linear, not exponential. **The deliverable of US-7.1 is the deletion of four files**, not the
creation of `WeaponDecorator`.

## Order of work

1. `docs/evidence.week9.md` Parts 0–2 — **before you write any code.** Graded.
2. **US-6.7** — the Sprint 6 carry-over. One point, under an hour, unrelated to this week.
3. US-7.1 — the decorator hierarchy, then the deletions.
4. US-7.2 — the registry, then the two-line fix to the equipped hand.
5. US-7.3 — the fourth enchantment, committed on its own so the `git diff --stat` is clean.
6. US-7.4 — two tests and a paragraph.
7. `docs/decorator-clinic.md` Parts D1–D4.

## Definition of Done

`docs/definition-of-done.md`, unchanged since Week 2.

## A warning about branch hygiene

US-7.3's acceptance criterion is a `git diff --stat`. If that commit contains anything besides
`KeenWeapon.java` and one line of `EnchantmentRegistry.java`, the measurement is worthless and
you will have to redo it. Commit US-7.2 fully before you start US-7.3.
