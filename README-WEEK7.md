# Lab 7 — Command — Starter

## The big change: the game is interactive

Until now `Main` ran an automated delve. **Now you type and things happen.** That one change
is what makes this week's pattern necessary.

## Provided for you (not the pattern lesson)

| Thing | Why |
|---|---|
| `Room.exits` + `GameWorld.linkRooms()` | So "move" is a real action. Deliberately a straight corridor — Week 10's generators make it a graph. |
| `Player.inventory` — a plain `List<Item>` | So "take" and "use" mean something. **Week 13 turns this into a Composite.** Don't build that now. |
| `Combat.monsterTurns()` and `Combat.playerStrikes()` | Week 5's `fight()` ran a whole battle in one call, because nobody was typing. Split for you. |
| `Main.gameLoop()` | An interactive loop built from a growing if/else chain. **This is the problem.** |

## The problem

Open `gameLoop()`. Eleven verbs, one method, and answer these before you write any code:

1. Where would you add `equip`? And `save`? *(Another branch. Forever.)*
2. How would you make `n` mean `north`? *(Another condition on every branch.)*
3. **How would you add undo?** Stop and genuinely think about this one.
4. How would you record a session and replay it for a bug report?

**Question 3 is the one that matters.** Questions 1, 2 and 4 are inconvenience. Question 3 is
*impossible* — there is nothing in this design that remembers what happened, because an
if-branch is not a thing you can keep.

Fill in `docs/evidence.md` §1 first. It's graded.

## What you'll build

| Class | Role |
|---|---|
| `Command` | the interface — `execute()`, `undo()`, `getDescription()` |
| `AbstractCommand` | shared plumbing: snapshot, act, let monsters reply |
| `MoveCommand`, `AttackCommand`, `TakeCommand`, `DropCommand`, `UseItemCommand` | the verbs |
| `SimpleCommand` | read-only verbs (look, help, inventory) |
| `NoCommand` | **Null Object** — the parser never returns null |
| `MacroCommand` | one word, several commands |
| `CommandParser` | the **invoker**: a registry map, not an if/else chain |
| `CommandHistory` | the undo stack **and** the replay log |
| `TurnSnapshot` | the before-state that makes undo actually correct |
| `GameContext` | the receiver, bundled |

## The one rule for this week

> **Do Part D2 in the order it's written.** Build the naive undo first, watch it fail, and
> only then build the snapshot.
>
> The failure is the lesson. If you skip to the working version you'll learn the mechanics and
> miss the idea — which is that **Command gives you a hook for undo, not an answer.**

## Definition of Done

`docs/definition-of-done.md`, unchanged.
