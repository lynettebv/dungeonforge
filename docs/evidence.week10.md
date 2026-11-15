# Week 10 Evidence

Parts 1–3 and Part 5 are graded on the measurement, not the code, and all of them happen
**before** you write anything.

---

## 0. The Sprint 7 carry-over (US-7.5)

Before any Template Method work. `docs/sprint-08-plan.md` has the story.

```bash
grep -rn 'unwrap' src/ | grep -v 'public Weapon unwrap'
grep -rl 'WEEK 9 STARTER' src/main/java | wc -l
```

**Paste the output of the first command.** How many callers does `unwrap()` have? ______

**And its javadoc says it is "used by undo". Open `TurnSnapshot.java`. Is it?** ______

**One sentence on why dead code with a comment vouching for it is worse than dead code.**


**The second command prints a number.** ______ Open any one of those files and read its header.
**Name one sentence in it that is no longer true, and say what made it stop being true.**


### The limit you will find

Build `disenchant`, then run this in the game:

```
enchant shortsword with frost
enchant shortsword with flaming
disenchant shortsword
```

**What is in your hand now?** ______

**Now get to a weapon that is Flaming but not Frost, starting from Flaming(Frost(sword)), using
only `disenchant`.**

**Write down what happened and why.** *(One sentence. It is the same sentence as US-7.4's,
pointed the other way.)*


---

## 1. BEFORE — run the tests, then run the diagnostic three times

```bash
mvn test
mvn -q exec:java -Dexec.args="--report"
mvn -q exec:java -Dexec.args="--report --seed=3"
mvn -q exec:java -Dexec.args="--report --seed=7"
```

**How many tests pass?** ______

**Paste all three dungeon reports:**

```

```

**How many of the four levels satisfy every invariant, on each seed?**
default: ______   seed 3: ______   seed 7: ______

**Seed 7 shows a symptom the other two do not.** Look at the EXIT column. What does it mean for
a player who reaches that level?


**Now put the test count and the default-seed result in one sentence.** *(This is the most
important sentence you will write this week. Two points.)*

> ____________________________________________________________________

**The three reports disagree. Why?** *(Look at what decides whether a room gets monsters. Then say
what that implies about how long a bug like this can survive.)*


**What does all of this tell you about the test suite you have been building for eight weeks?**


---

## 2. BEFORE — find the drift

Open `core/GameWorld.java`. The three `build*Level` methods are 90% the same code. Put two of
them side by side in two editor windows.

**Fill this in by reading. Do not guess.**

| Step | Corridor | Cave | Ring |
|---|---|---|---|
| 1. create the rooms | | | |
| 2. connect them | own shape | own shape | own shape |
| 3. entrance flavour | | | |
| 4. populate — **which index does the loop start at?** | | | |
| 5. prune unreachable rooms | | | |

**Which step is genuinely different between the three?** ______

**Which differences are NOT about the shape of the level?**


**For each, say what the consequence is for a player:**

| Drift | What the player experiences |
|---|---|
| | |
| | |

**Neither of these is a typo. Both are what happens when one algorithm exists in three copies. In
one sentence: why did the compiler not help?**


**A faster way to find this class of bug, which you should keep:**

```bash
grep -n 'for (int r = [01]; r < rooms.size(); r++)' src/main/java/dungeonforge/core/GameWorld.java
```

**Paste the output. What does the pattern of 0s and 1s tell you?**


---

## 3. BEFORE — the counting

```bash
wc -l src/main/java/dungeonforge/core/GameWorld.java
grep -c 'new Room("L"' src/main/java/dungeonforge/core/GameWorld.java
grep -c 'createRoomFlavor()' src/main/java/dungeonforge/core/GameWorld.java
grep -c 'populatorFor(' src/main/java/dungeonforge/core/GameWorld.java
grep -c 'switch (d % ' src/main/java/dungeonforge/core/GameWorld.java
```

| | Before | After |
|---|---|---|
| `GameWorld.java` lines | | |
| times room creation is written | | |
| times entrance flavour is written | | |
| times the populate loop is written | | |
| `switch` statements over level shape | | |
| levels satisfying every invariant (seed 3) | | |

*(Fill the After column at the end of the sprint. Nothing in it may be a guess.)*

---

## 4. AFTER — US-8.4, the receipt

**Paste `git diff --stat HEAD~1` for the commit that added the fourth shape:**

```

```

**New files:** ______  **Modified files:** ______  **Lines added:** ______

**Is `GameWorld.java` in that diff?** ______ **Why not?**


### The other half of the receipt — what the old design would have cost

**(a)** To add a spoke shape to the starter, which existing method would you have copied?
*(Be honest — you would copy the one whose shape is closest, not the one that is correct.)*

______

**(b)** Roughly how many lines would that have added to `GameWorld.java`? ______

**(c)** **The question that matters.** Look at the method you named in (a). Run the `grep` from
Part 2 on it. Does it have any of the drift you catalogued?

______

**So what would have happened to the new level's invariants?**


**(d)** Now check your answer against reality. Your instructor measured it: the fourth shape was
added to the starter by copying `buildRingLevel`, and on **the default seed** the report went
from `orphaned rooms: 4, spawn-camped levels: 0` to `orphaned rooms: 4, spawn-camped levels: 1`
— **the newly spawn-camped level being level 4, the one that had just been added.**
`GameWorld.java` went from 269 lines to 317, the count of populate loops starting at `r = 0`
went from 1 to 2, and **it compiled on the first attempt.**

> Write the conclusion as a sentence with the word *propagate* in it.

---

## 5. AFTER — what you did not write

Your fourth shape produces levels with no orphaned rooms and an empty entrance.

**Find the reachability check in `SpokeGenerator.java`.** Where is it? ______

**Find the entrance guard in `SpokeGenerator.java`.** Where is it? ______

**In two or three sentences: explain how a class can satisfy an invariant it does not implement,
and what had to be true of `generate()` for that to work.**


---

## 6. AFTER — the honest accounting

Two of the starter's levels were broken. Both are fixed now.

**Did you write a bug fix? Quote the lines if you did.**


**If you did not, describe what you actually did instead.** *(There is a precise way to say this,
and it involves the word "deleted".)*


---

## 7. Retro line

**First, the carry-over.** One sentence on what `disenchant` taught you about Decorator that
building the decorators did not.




**One sentence on something Template Method gave you, and one on something it took away.** *(It
does take something. Head First is clear about it on p. 302 — a subclass cannot change the order
of the steps, and one day you will want to.)*
