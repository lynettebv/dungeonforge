# Lab 5 — Strategy & Observer — Starter

## What's new in the starter

**`core/Combat.java`** — an encounter resolver, so monsters finally have something to do.
`Main` now runs a full delve: descend, fight everything, report.

It works. It also contains this week's two problems.

### Problem 1 — every monster fights identically

```java
private void monsterActs(Monster m, Player player) {
    int damage = m.getAttackPower();
    player.takeDamage(damage);
    System.out.println("      " + m.getName() + " hits you for " + damage);
}
```

A Bone Priest, an Imp and a Crypt Rat are the same opponent with different names. And a
badly wounded monster cannot start running away, because "how this thing fights" isn't a
thing at all — it's code baked into the encounter loop.

> **The obvious fix is to subclass `Monster`.** Before you do, go and fill in Part D1 of
> `docs/strategy-observer-clinic.md`. It takes ten minutes and it will change your mind.

### Problem 2 — `Combat` knows about the console

Count the `System.out` calls. Now imagine adding quests, achievements, and a scrolling log.
All three would be bolted into this one class, and it would grow forever.

## Do this first

```bash
mvn -q exec:java > before.txt
```

Fill in `docs/evidence.md` §1, and **do Part D1 of the clinic before writing code.** Both are
graded.

## What you'll build

| Package | Classes | Pattern |
|---|---|---|
| `behavior` | `Action`, `CombatStrategy` + 4 implementations | **Strategy** |
| `events` | `EventType`, `GameEvent`, `GameEventListener`, `EventBus` | **Observer** (the machinery) |
| `events` | `Quest`, `QuestTracker`, `AchievementSystem`, `CombatLog` | **Observer** (the subscribers) |

Then refactor `Combat` so that it **decides nothing about behaviour and prints nothing**.

## Two design rules for this week

> **1. A strategy returns a decision; it does not perform one.**
> `chooseAction()` returns an `Action`. `Combat` carries it out. That separation is what lets
> you unit-test a strategy with no running game.

> **2. `Combat` must not contain the name of any listener class, and must not print.**
> A test greps for both. That absence is the deliverable of US-3.4.

## A correction carried over from Week 4

`Monster`'s constructor used to add random stat variance, and so did `MonsterFactory` — so
every monster was rolled twice, and no test could construct a monster with exact hit points.
Variance is a *creation* concern, so it now lives only in the factory. A `Monster` is exactly
what it was constructed with.

Small thing, but it's the kind of duplication that appears when responsibility moves between
classes over several weeks. Worth noticing.

## Definition of Done

`docs/definition-of-done.md`, unchanged.
