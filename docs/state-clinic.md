# The Twins Clinic — Lab 8, Part D

> Week 5 you were asked to guess what could possibly distinguish State from Strategy, given
> that their UML is nearly identical. **This is where you find out.**

## D1 — Grade your own Week 5 prediction · 8 pts

Open `docs/strategy-observer-clinic.md` from Week 5 and find your D4 answer.

**Paste it here, unedited:**

```
I can guess that the differences may lie in how changes happen to the object.
So for Strategy the object can choose between multiple behaviors.
For State, the object may change because the implementations happen to the object.
```

**Now, having built both, mark your own work:**

| | Your Week 5 guess | What you would say now |
|---|---|---|
| Who decides which one is active? | the client - factory assigns it from data| the object - next() returns own successor|
| Do the alternatives know about each other? | no bc AggressiveStrategy has never heard of SkittishStrategy | Yes bc HuntingState.next() names Enraged, Fleeing, & Dead |
| What question does it answer about a monster? | BEHAVIOR - HOW the monster fights| STATUS - WHEN it fights, rages, runs, or stops existing|
| Can it change while the object is alive? | rarely, only if someone swaps it | constantly, & swaps itself |

**How close were you?** Circle one and justify it in a sentence:
*dead on · half right · reasonable but wrong · nowhere near*

-- Half right because I understood that Strategy was about choosing behaviors and State was about changes to an object, 
but I didn't fully understand how each pattern handles those changes.

 -- Strategy is about behavior & state is about status!

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
| What stops someone typing `setStateName("stnuned")` and getting no error? | Nothing because the state is stores as a String. |

> Revert this experiment before continuing. It exists to be measured, not kept.

### Then, on YOUR refactored code

Add the same `StunnedState`.

| Question | Your answer |
|---|---|
| New files | StunnedState.java |
| Existing files modified | |
| Did `Combat.java` change? | no |
| Did `Monster.java` change? | no |

**Paste `git diff --stat`:**

```

```

**One paragraph: what did the compiler start doing for you that it could not do when the
state was a string?**
With the State Pattern, the compiler can now catch mistakes that it couldn't when states were stored as Strings.
Before, I could misspell a state name and the program would still compile,
but the behavior might not work correctly. Now each state is an object
that implements the MonsterState interface, so the compiler can check
that the required methods exist and that I'm using the correct types.
This makes adding new states safer and easier to maintain.


## D3 — Rejected versus impossible · 7 pts

In Week 7, typing `north` during a fight got you told off. In Week 8 it is not on the menu.

**Paste your Week 7 behaviour (from `git show` on an old tag, or just describe it):**

```
In Week 7, the comman was recognized but MoveCommand checked whether monsters were alive and refused to move.
```

**Paste your Week 8 behaviour, including the `help` output in both modes:**

```
[Combat | HP 80/80] > n

  You cannot do that in Combat mode. 
 Available here: attack, look, use, inventory, status, history, undo, help, quit

[Combat | HP 80/80] > 

-- HELP in Combat
[Combat | HP 80/80] > help

  [Combat] you can: attack, look, use, inventory, status, history, undo, help, quit

-- HELP in Exploring
[Exploring | HP 78/80] > help

  [Exploring] you can: north, south, down, look, take, loot, drop, use, inventory, pack, status, quests, history, undo, help, quit

```

### The questions

**1.** Count the mode guards you deleted from command classes: __3__
Which files were they in? MoveCommand.java, TakeCommand.java, DropCommand.java

**2.** `help` now prints a different list in each mode, and you did not write any code to make
that happen. **Why not?**
Because help now gets its commands from ctx.allowedVerbs(), which returns the commands available in the current state. 
Each state already defines its own list, so I don't need separate conditionals in HelpCommand.

**3.** Here is the one that matters. Suppose a teammate adds an `equip` command next week and
forgets to think about combat. **In the Week 7 design, what goes wrong? In the Week 8 design,
what goes wrong?** Be specific about *when* the mistake would be noticed.

In Week 7, the new equip command could accidentally work during combat if the developer forgot to add a guard. 
The mistake might not be noticed until someone actually tried using it during a fight.

In Week 8, the command wouldn't be available unless it was added to a state's allowed commands.
The mistake would be noticed when someone tried using equip and the parser rejected it,
rather than accidentally allowing it during combat.

The important difference is that Week 7 could accidentally allow an invalid command, 
while Week 8 defaults to rejecting new commands.


## D4 — One honest question · 3 pts

`GameContext.syncState()` deliberately does **not** pull the player out of the pack screen
when a monster arrives.

**Is that the right call?** Argue either side in a short paragraph. There is no answer key —
real games disagree about this, and knowing that a design decision is *contested* is worth
more than picking the popular side.

I think keeping the player in Pack mode when a monster arrives is the right decision because it makes the game easier
and gives the player a safe place to manage their inventory. I'm not the best gamer, so I don't mind some help.
The player can take their time deciding what items to use without worrying about being attacked or forced into Combat mode.
I think this makes the game less frustrating and gives the player more control over when they are ready to fight.

**And anything still unclear:**

