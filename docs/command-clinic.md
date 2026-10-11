# The Undo Clinic — Lab 7, Part D

> Week 3: refuse a pattern. Week 4: tell three lookalikes apart. Week 5: two directions of
> decoupling. **This week: discover that a pattern gives you a hook, not an answer.**

## D1 — Measure the if/else · 6 pts

**Before you delete anything**, on `main`:

| Question | Your answer |
|---|---|
| Lines in `Main.gameLoop()` | |
| Number of `else if` branches | |
| To add "equip", which file(s) and how many places? | |
| To add alias "e" for "equip", how many *additional* places? | |
| Which line of `gameLoop()` would you attach undo to? | |

That last row has no answer, and saying so clearly is worth full marks.

**Now finish the refactor and measure again:**

| Question | Your answer |
|---|---|
| Lines in the new `gameLoop()` | |
| `else if` branches remaining | |
| To add "equip": files created, files modified | |
| To add alias "e": how many places? | |

## D2 — The naive undo, and why it fails · 12 pts 🔑

**Do these steps in order. Do not skip step 1 — the failure is the lesson.**

### Step 1 — build the obvious version

Give `AttackCommand` a naive undo: a single field remembering the target's hit points before
the blow, restored on `undo()`. No snapshot, no other state.

```java
private int targetHpBefore;      // the "obvious" approach
```

### Step 2 — break it, on purpose

Write down `HP`, `XP` and `Gold` from `status`. Attack something that survives and hits back.
Check `status` again. Now `undo`, and check `status` a third time.

| | HP | XP | Gold | Monster HP |
|---|---|---|---|---|
| Before attack | | | | |
| After attack | | | | |
| After naive undo | | | | |

**Which values did NOT come back?**

**Why not? Answer in terms of what happened during that one turn**, not in terms of code:


### Step 3 — list everything one turn can change

Before you write the fix, enumerate it. Your undo has to reverse all of it.

- [ ] the target monster's hit points
- [ ] 
- [ ] 
- [ ] 
- [ ] 
- [ ] 

> Two of these are easy to miss. One involves Week 5's strategy swap; the other involves a
> monster that isn't in the room any more.

### Step 4 — fix it, then answer this

Build `TurnSnapshot` and make `undo()` restore it. Re-run the table from step 2 and confirm
everything returns.

**Now the question worth the marks.** The Command pattern promised "supports undoable
operations." **Did Command actually give you undo?** Answer carefully — there's a real
distinction here, and it's the most useful thing in this week.


## D3 — The capabilities you didn't ask for · 5 pts

The pattern's definition lists four things. You wanted one.

**Record a session.** Play for a dozen commands, run `history`, and paste the log:

```

```

**Save those lines to a file and replay them:**

```bash
mvn -q exec:java -Dexec.args="--script=mysession.txt"
```

**Did you get the same game? Why does that depend on something you built in Week 3?**


**Finally:** name a **fifth** capability that falls out of actions-as-objects which the
pattern's definition doesn't mention. *(Hint: think about the optional GUI track, or about
anything that isn't a keyboard.)*


## D4 — One honest question · 2 pts

`AbstractCommand.execute()` is `final` and defines a fixed order: snapshot, act, monster
turns. Subclasses fill in one step.

**You have seen this shape before this week, in a pattern you already know. Which one, and
where?** *(You built something structurally identical in Week 4.)*


**And anything still unclear:**

