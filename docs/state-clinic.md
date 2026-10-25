# The Twins Clinic — Lab 8, Part D

> Week 5 you were asked to guess what could possibly distinguish State from Strategy, given
> that their UML is nearly identical. **This is where you find out.**

## D1 — Grade your own Week 5 prediction · 8 pts

Open `docs/strategy-observer-clinic.md` from Week 5 and find your D4 answer.

**Paste it here, unedited:**

```

```

**Now, having built both, mark your own work:**

| | Your Week 5 guess | What you would say now |
|---|---|---|
| Who decides which one is active? | | |
| Do the alternatives know about each other? | | |
| What question does it answer about a monster? | | |
| Can it change while the object is alive? | | |

**How close were you?** Circle one and justify it in a sentence:
*dead on · half right · reasonable but wrong · nowhere near*


> **You are not graded on having been right.** You are graded on the honesty of the comparison
> and on what you can now articulate that you could not in Week 5.

## D2 — What does a new state cost? · 7 pts

The Week 7 code stored a monster's state as a **string** with conditionals in three places.
Your Week 8 code stores it as an **object**. Measure the difference.

### First, on the STARTER code (before your refactor)

Add a `"stunned"` state that lasts one turn and prevents acting.

| Question | Your answer |
|---|---|
| Which methods in `Combat` did you have to edit? | |
| How many conditionals did you add or change? | |
| What stops someone typing `setStateName("stnuned")` and getting no error? | |

> Revert this experiment before continuing. It exists to be measured, not kept.

### Then, on YOUR refactored code

Add the same `StunnedState`.

| Question | Your answer |
|---|---|
| New files | |
| Existing files modified | |
| Did `Combat.java` change? | |
| Did `Monster.java` change? | |

**Paste `git diff --stat`:**

```

```

**One paragraph: what did the compiler start doing for you that it could not do when the
state was a string?**


## D3 — Rejected versus impossible · 7 pts

In Week 7, typing `north` during a fight got you told off. In Week 8 it is not on the menu.

**Paste your Week 7 behaviour (from `git show` on an old tag, or just describe it):**

```

```

**Paste your Week 8 behaviour, including the `help` output in both modes:**

```

```

### The questions

**1.** Count the mode guards you deleted from command classes: ____
Which files were they in?

**2.** `help` now prints a different list in each mode, and you did not write any code to make
that happen. **Why not?**

**3.** Here is the one that matters. Suppose a teammate adds an `equip` command next week and
forgets to think about combat. **In the Week 7 design, what goes wrong? In the Week 8 design,
what goes wrong?** Be specific about *when* the mistake would be noticed.


## D4 — One honest question · 3 pts

`GameContext.syncState()` deliberately does **not** pull the player out of the pack screen
when a monster arrives.

**Is that the right call?** Argue either side in a short paragraph. There is no answer key —
real games disagree about this, and knowing that a design decision is *contested* is worth
more than picking the popular side.


**And anything still unclear:**

