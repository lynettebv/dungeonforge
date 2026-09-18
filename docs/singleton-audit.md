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
> If two instances would produce *incorrect behavior* — not just wasted memory, not just
> inconvenience — the class may deserve to be a Singleton.
> If two instances would merely be *odd*, it is a dependency, and you should pass it in.

Fill in every row. Two of the eight are genuine singletons; you already know which, because
you built them this week. Your job is to defend the other six answers.

| # | Class | What it does | Singleton? | Would a 2nd instance be a bug, or just unusual? Why? |
|---|---|---|--------|------------------------------------------------------|
| 1 | `GameConfig` | Holds every tunable setting | x      |                                                      |
| 2 | `RandomSource` | The one seeded RNG | x      |                                                      |
| 3 | `Player` | The player character |        | x                                                    |
| 4 | `MonsterFactory` (Wk 4) | Turns blueprints into monsters |        | x                                                    |
| 5 | `EventBus` (Wk 5) | Publishes game events to subscribers |        | x                                                    |
| 6 | `CommandHistory` (Wk 7) | The undo stack |        | x                                                    |
| 7 | `SaveSystemFacade` (Wk 12) | Reads and writes save files |        | x                                                    |
| 8 | `Logger` | Writes diagnostic output to a file |        | x                                                    |

## The three that will cause arguments

Rows 5, 7 and 8 are the interesting ones, and reasonable engineers disagree about all three.
Pick **one** of them and write a paragraph:

**Which one:** ___Logger___

**The case FOR making it a Singleton:**

The Singleton pattern offers a convenient and efficient way to manage logging
and access the same Logger anywhere in our code. This can keep logging behavior consistent.

**The case AGAINST:**

Besides the downside that Singletons make unit testing harder,
making Logger a Singleton can make code harder to maintain. Because of issues
with global access, Logger may easily take on responsibilities that don't belong to it.

**What you would actually do in this project, and why:**

For this project, I would avoid making Logger a singleton and potentially complicating unit testing even more.
GameConfig and RandomSource are enough, I do not want to over-apply the pattern by creating a third one.


> There is no answer key for this paragraph. You are graded on whether you engaged with the
> tension, not on which side you landed.

## One more question

Your `GameConfig` has a method called `resetForTests()`. It exists only so that tests can
undo the global state that the Singleton created.

**In one or two sentences: what is that method telling you about the pattern?**

This method is highlighting how a Singleton makes your code harder to unit test.
You have to straight up undo the state your Singleton maintains just to get your tests to run correctly.
