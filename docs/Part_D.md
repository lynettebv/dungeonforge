# Lab 8 Part D

## Part D1 — the comparison table

If you guessed "one is about behavior and one is about status" in Week 5, YOU were closer
than you realized!!!

| | Strategy (Wk 5) | State (Wk 8) |
|---|---|---|
| Who decides which is active? | the **client** — the factory assigns it from data | the **object** — `next()` returns its own successor |
| Do the alternatives know each other? | **No.** `AggressiveStrategy` has never heard of `SkittishStrategy`. | **Yes.** `HuntingState.next()` names `Enraged`, `Fleeing` and `Dead`. |
| What does it answer? | **HOW** the monster fights | **WHEN** it fights, rages, runs, or stops existing |
| Does it change while alive? | rarely, and only if someone swaps it | constantly, and it swaps itself |

*states know about each other; strategies do not.*
This is visible in the code, `next()` returns another state by name and it is the reason the
UML looks identical while the intent is opposite.

## Part D2 — the paragraph

**the compiler now checks the state machine.** With a string, `"stnuned"` is a
valid string and the mistake surfaces at runtime, in one branch, maybe. With classes, a typo
is a missing symbol and the build fails.

Note: *transitions* also became checkable, `next()` must return
a `MonsterState`, so you cannot transition to something that doesn't exist.

## Part D3 — question 3

1. Deleted 3 conditional guards alone in Command classes that guarded against commands in rooms with live monsters.
2. Help became context sensitive for free because once each state encapsulated its VERBS, the CommandParser could just delegate to the context's state.
3. *A teammate adds `equip` and forgets about combat.*

- **With our Week 7 design:** the command works fine during a fight. Nothing complains. The bug ships,
  and someone finds it in playtesting — or a player finds it and abuses it.
- **Week 8 design:** the verb is not in any `GameState`'s list, so `equip` doesn't work **at
  all**, anywhere. The mistake is obvious and immediate on the very first test.

Take away:
* A rule you can forget to apply fails silently and late
* A rule you must opt into fails loudly and early. This is beTtEr!!!
