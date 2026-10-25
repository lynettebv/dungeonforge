# Lab 8 — State — Starter

## Two problems, one pattern

### Problem A — every command re-checks the mode

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

`MoveCommand`, `TakeCommand` and `DropCommand` each open with the same guard. Three copies of
one rule. Add `equip` next week and there will be four — or three, if whoever writes it
forgets, and then you can equip armour mid-swing.

And `help` still lists every verb in the game regardless of whether you can use it.

### Problem B — the monster lifecycle is a string

`Monster.stateName` holds `"idle"`, `"hunting"`, `"enraged"`, `"fleeing"` or `"dead"`. Three
methods in `Combat` branch on it:

| Method | Asks |
|---|---|
| `updateMonsterState()` | the transitions, as nested ifs |
| `monsterActs()` | "may this monster act?" |
| `damageFrom()` | "is it enraged?" |

**This is exactly where Chapter 10 of your textbook begins.** The Gumball Machine starts with
an `int state` and conditionals in every method, and the chapter spends forty pages taking it
apart. Read the chapter first; the parallel is almost exact.

## Do this first

Fill in `docs/evidence.md` §1, and answer the three questions at the bottom of Problem B
**before** you write any code. Then do **Part D2's first half** — add a `"stunned"` state to
the *starter* code and count what you had to edit. Revert it afterwards.

Measuring the bad version is how you find out what the good version bought you.

## What you'll build

| Package | Classes | Level |
|---|---|---|
| `states` | `MonsterState` + `Idle`/`Hunting`/`Enraged`/`Fleeing`/`Dead` | the entity lifecycle |
| `states` | `GameState` + `Exploring`/`Combat`/`Inventory`/`GameOver` | the game's modes |

Then: `GameContext` holds the current `GameState`, `CommandParser` refuses anything the state
does not list, and **the three scattered guards get deleted**.

## The rule for this week

> **`Combat` must not contain the name of any monster state, and no command may check the
> game mode.**

Both are grep-able, and there are tests for both. The deletions are the deliverable.

## A warning about the obvious shortcut

You may be tempted to make `MonsterState` an `enum` with a `switch`. **That is the string
field with extra steps** — you still get one big conditional, just relocated. Part D2 measures
the difference, so build the object version and find out for yourself.

## Definition of Done

`docs/definition-of-done.md`, unchanged.
