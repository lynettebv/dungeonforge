# The Singleton Audit — Lab 3, Part C

> **The hard part of Singleton week is not writing one. It is 12 lines of code.**
> The hard part is knowing when *not* to.
>
> Singleton is the most over-applied pattern in the book. A student who leaves this week able
> to write one has learned the easy half. A student who leaves able to *refuse* to write one
> has learned the half that matters.

Below are **eight** candidate classes from DungeonForge's future. Three you have already met;
five arrive in Weeks 4 to 15. For each, decide: **Singleton, or not?**

Answer with the test we will use all semester:

> **Would a second instance be a BUG, or merely unusual?**
>
> If two instances would produce *incorrect behaviour* — not just wasted memory, not just
> inconvenience — the class may deserve to be a Singleton.
> If two instances would merely be *odd*, it is a dependency, and you should pass it in.

Fill in every row. Two of the eight are genuine singletons; you already know which, because
you built them this week. Your job is to defend the other six answers.

| # | Class | What it does | Singleton? | Would a 2nd instance be a bug, or just unusual? Why? |
|---|---|---|---|---|
| 1 | `GameConfig` | Holds every tunable setting | | |
| 2 | `RandomSource` | The one seeded RNG | | |
| 3 | `Player` | The player character | | |
| 4 | `MonsterFactory` (Wk 4) | Turns blueprints into monsters | | |
| 5 | `EventBus` (Wk 5) | Publishes game events to subscribers | | |
| 6 | `CommandHistory` (Wk 7) | The undo stack | | |
| 7 | `SaveSystemFacade` (Wk 12) | Reads and writes save files | | |
| 8 | `Logger` | Writes diagnostic output to a file | | |

## The three that will cause arguments

Rows 5, 7 and 8 are the interesting ones, and reasonable engineers disagree about all three.
Pick **one** of them and write a paragraph:

**Which one:** ______

**The case FOR making it a Singleton:**


**The case AGAINST:**


**What you would actually do in this project, and why:**


> There is no answer key for this paragraph. You are graded on whether you engaged with the
> tension, not on which side you landed.

## One more question

Your `GameConfig` has a method called `resetForTests()`. It exists only so that tests can
undo the global state that the Singleton created.

**In one or two sentences: what is that method telling you about the pattern?**


