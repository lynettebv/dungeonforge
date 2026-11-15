# Sprint 8 Plan — Week 10 — Template Method — SUPPLIED

**Epic:** E9, Level generation · **Pattern:** Template Method (Head First Ch. 8)

> **Still supplied — stories, acceptance criteria and sub-tasks**, each sub-task tagged `[AC3]`
> for the criterion it serves. Ten worked sprints now. Read the tags.
>
> **This sprint opens with a carry-over, US-7.5.** It is listed first because that is what
> carried-over work does: it arrives before the new work and spends capacity you were hoping to
> use on something else.

## Sprint Goal

> Building a level is one algorithm with one order of steps, written once. A level shape gets to
> choose the shape and nothing else — and cannot skip a step, because it was never asked to
> remember one.

## Capacity

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 3 (Wk 5) | 10 | ____ |
| Sprint 5 (Wk 7) | 10 | ____ |
| Sprint 6 (Wk 8) | 12 | ____ |
| Sprint 7 (Wk 9) | 11 | ____ |
| **Sprint 8 (Wk 10)** | **12** | ____ |

**Three of those Completed columns are empty, and that is now the problem.** Five sprints in,
there is no measured velocity to plan against, so "capacity ~10" is still a guess from Week 5.
Part E asks you to fill the row for Sprint 7 *and* Sprint 8 — estimated against actual hours,
per story. A planning number you have never checked is a wish.

**If Sprint 7 came in under 9 points, take `tryConnect` as provided code and drop US-8.3**,
which brings this sprint to 11. **Do not drop the carry-over** — debt deferred twice stops being
debt and becomes the design.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| **US-7.5** | **Carried over from Sprint 7** — take an enchantment back off | **2** |
| US-8.1 | One algorithm for building a level, written once | 4 |
| US-8.2 | Steps a shape must answer, may answer, cannot touch | 3 |
| US-8.3 | A shape cannot spend a direction the staircase needs | 1 |
| US-8.4 | A fourth shape, and the receipt for it | 2 |

**Capacity:** ~10 points · **Committed:** 12 points (10 new + **2 carried over**)

## Suggested order

0. **US-7.5 first.** Two points, nothing to do with Template Method, and it is debt. About an
   hour, and it gets the board honest before the real work starts.
1. **US-8.1 second, and it is most of the sprint.** **The deletion of the three builders is the
   deliverable.**
2. **US-8.2** — sort the steps into fixed, primitive and hook. Mostly thinking, little typing.
3. **US-8.3** — one shared helper and a test.
4. **US-8.4** — twenty minutes, and it produces your Part D evidence.

---

# US-7.5 — Take an enchantment back off · 2 pts · CARRIED OVER FROM SPRINT 7

**As a** player
**I want** to strip an enchantment off a weapon
**So that** a decision I made three rooms ago is not permanent.

### Why this is carry-over and not a feature request

Sprint 7 left a public method on `WeaponDecorator`:

```java
/** The weapon underneath. Used by undo, and by nothing else. */
public Weapon unwrap() { return wrapped; }
```

Check for yourself before you touch anything:

```bash
grep -rn 'unwrap' src/ | grep -v 'public Weapon unwrap'
```

**Nothing.** Undo did not call it either -- `TurnSnapshot` stores the whole weapon object, so it
never needed to peel a layer off anything. The comment asserted a caller that does not exist.

Dead code is a small problem. **Dead code with a comment vouching for it is a worse one**,
because the next person reads the comment, believes the method is load-bearing, and leaves it
there. That is how a codebase accumulates furniture nobody will move.

So the method either goes or earns its keep. This story makes it earn its keep.

### What you will find out by building it

`disenchant` can remove the **outermost** enchantment and only that one. Given

```
Flaming(Frost(Notched Shortsword))
```

you can get to `Frost(Notched Shortsword)`, and from there to the bare sword. There is **no**
sequence of calls that removes the Frost layer and leaves the Flaming one on, because no
decorator can reach past the one wrapped around it.

US-7.4 proved that wrapping order changes the numbers going **in**. This is the same constraint
coming **out**: *last on, first off.* AC8 makes you assert it, and D4 asks you to say what it
means. If a feature needs arbitrary-layer removal, Decorator is the wrong pattern for that
feature -- and being able to say that about a pattern you just spent a week building is the
point of saying it.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `Weapon`, **then** it declares `default Weapon peel() { return this; }` -- a plain weapon peels to itself. |
| **AC2** | **Given** `WeaponDecorator`, **then** it overrides `peel()` to return what it wraps, and `unwrap()` is **gone**. |
| **AC3** | **Given** `disenchant <weapon>`, **then** it is registered exactly once and is reachable in Exploring mode. |
| **AC4** | **Given** `DisenchantCommand`, **when** I strip its comments and search it, **then** there are **zero** matches for `instanceof WeaponDecorator`. |
| **AC5** | **Given** the weapon in my hand, **when** I disenchant it, **then** `status` shows the lower damage **in the same turn**. |
| **AC6** | **Given** a `disenchant` I just ran, **when** I `undo`, **then** inventory and hand both hold the enchanted weapon again -- **and** `DisenchantCommand` has no `undo()` of its own. |
| **AC7** | **When** I run `grep -rl 'WEEK 9 STARTER' src/main/java \| wc -l`, **then** the answer is **0**. |
| **AC8** | **Given** `Flaming(Frost(sword))`, **when** I disenchant once, **then** a test asserts the result is **`Frost Iron Sword` with damage 11** -- and a second test shows that `Flaming Iron Sword` is unreachable by any number of disenchants. |
| **AC9** | **When** I run `mvn test`, **then** all 110 tests that passed before still pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC1]** Add `default Weapon peel() { return this; }` to `Weapon`. **Do this first** --
      nothing else compiles against it until it exists.
- [ ] **T2 [AC2]** Override `peel()` in `WeaponDecorator` to return `wrapped`, and **delete
      `unwrap()`**. One method replaces the other; you are not adding a second way to do it.
- [ ] **T3 [AC3]** Write `DisenchantCommand`, modelled on `EnchantCommand`. It extends
      `AbstractCommand` and implements `doExecute()`.
- [ ] **T4 [AC4]** Detect "nothing to remove" by **identity** -- `peeled == w` -- not by asking
      what type you are holding. See the note below; this is the design question of the story.
- [ ] **T5 [AC5]** The same two lines `EnchantCommand` needs: capture
      `wasEquipped = (player().getEquippedWeapon() == w)` **before** the swap, re-`equip` after.
      That makes **two copies of those two lines**. A third would be the moment to move them
      onto `Player`; note it in your retro rather than doing it now.
- [ ] **T6 [AC3]** Register the verb and add it to `ExploringState`'s list. `d` is already
      `down` and `e` is already `equip`, so this verb gets **no short form** -- the third
      collision the Week 7 registry has shown you instead of swallowing.
- [ ] **T7 [AC6]** **Write no `undo()`.** Run the undo test and work out why it passes. If you
      write one, you will restore twice and the test will tell you so.
- [ ] **T8 [AC8]** The two order tests. The second one is unusual: it asserts something that
      **never appears**. Say so in a comment, because a reader will otherwise think it is
      checking the wrong thing.
- [ ] **T9 [AC7]** Fix the stale comments. **Ten** files in `src/main/java` still describe
      themselves as `WEEK 9 STARTER` code. Three of them carry one identical sentence calling
      the class a fire enchantment -- which is true of `FlamingWeapon` and **false of
      `FrostWeapon` and `VampiricWeapon`**, because the sentence was copied and never corrected.
      `BasicWeapon` tells you to go and open a file that US-7.1 deleted.
      ```bash
      grep -rl 'WEEK 9 STARTER' src/main/java          # 10 before, 0 after
      grep -rl 'a weapon that burns' src/main/java     # 3 before, 1 after
      ```
- [ ] **T10 [AC9]** `mvn test`. Commit on `feature/us-7.5-disenchant`.

> **T4, and the question it is really asking.**
>
> The obvious implementation is:
>
> ```java
> if (w instanceof WeaponDecorator d) { Weapon under = d.unwrap(); ... }
> ```
>
> It works. US-7.1's AC6 would even still pass, because that grep only looks inside `items/`.
> Do it the other way anyway: put `peel()` on the interface, give it a default body of
> `return this`, and let a plain weapon answer the question for itself.
>
> **The type test you want to write is usually a method you forgot to put on the interface.**
> You have now built that argument twice -- Week 4's factory was the first time.
>
> **T9 is not tidying, and it is graded.** This is the drift week. Code that has been fixed and
> commentary that has not is the same failure as two builders that no longer agree -- except
> nothing will ever fail a test over it. Three separate files name the wrong enchantment for
> exactly the reason the ring builder populates the wrong room: **somebody copied the nearest
> similar thing.** Write that sentence in your retro.

---

# US-8.1 — One algorithm for building a level, written once · 4 pts

**As a** developer
**I want** the order of the level-building steps to exist in exactly one place
**So that** a new level shape cannot silently forget a step, the way two of the current three
already have.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** a `dungeonforge.generate` package, **then** it contains an abstract `LevelGenerator`. |
| **AC2** | **Given** `LevelGenerator`, **then** `generate(...)` is **`final`**. |
| **AC3** | **Given** `LevelGenerator`, **then** `connectRooms(...)` is **abstract**. |
| **AC4** | **Given** the `generate` package, **then** there is one class per shape, each overriding `connectRooms` and nothing structural. |
| **AC5** | **When** I run `grep -c 'private DungeonLevel build' core/GameWorld.java`, **then** the answer is **zero**. |
| **AC6** | **When** I run `grep -c 'switch (d % ' core/GameWorld.java`, **then** the answer is **zero**. |
| **AC7** | **When** I grep the project for `new Room("L"`, **then** it appears in **exactly one file** (it starts in three places in one file). |
| **AC8** | **When** I grep for `createRoomFlavor()` outside the theme kits, **then** it appears **once** (it starts at three). |
| **AC9** | **Given** `GameWorld.java`, **then** it is under **120 lines** (it starts at 269). |
| **AC10** | **When** I run `--report` on **both** the default seed and `--seed=3`, **then** every level shows **0 orphans and 0 spawn** on both. |
| **AC11** | **When** I run `mvn test`, **then** all 110 tests that passed before still pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC1]** Make the package `dungeonforge.generate` and an abstract class
      `LevelGenerator` in it.
- [ ] **T2 [AC2, AC7, AC8]** Write `generate(depth, theme, roomCount, isFinalLevel)` and mark it
      **`final`**. Its body is seven calls in order and nothing else. Copy the step order from
      `buildCorridorLevel` — that is the copy that is correct.
- [ ] **T3 [AC7]** Move room creation into a `private createRooms(...)`.
- [ ] **T4 [AC8]** Move the entrance handling into a `private markEntrance(...)`.
- [ ] **T5 [AC10]** Move the populate loop into a `private populateRooms(...)`, **starting at
      `r = 1`**. Bring `populatorFor(...)` with it.
- [ ] **T6 [AC10]** Move the breadth-first prune into a `private enforceReachability(...)` and
      call it **last** from the template. Private is deliberate — see US-8.2 T4.
- [ ] **T7 [AC3]** Declare `protected abstract void connectRooms(DungeonLevel level)` and
      `public abstract String shapeName()`.
- [ ] **T8 [AC4]** Write `CorridorGenerator`, `CaveGenerator`, `RingGenerator`. Each is a
      `connectRooms` and a `shapeName`. **If any is longer than about twenty lines, you have
      moved something that should have stayed in the template.**
- [ ] **T9 [AC6]** Write `GeneratorRegistry` — one line per shape, `forDepth(depth)` cycling
      through them. Model it on Week 4's `ThemeRegistry`; you have built this before.
- [ ] **T10 [AC5, AC9]** **Delete `buildCorridorLevel`, `buildCaveLevel`, `buildRingLevel`, the
      `switch`, and `pruneUnreachable` from `GameWorld`.** Replace `generate()` with a loop that
      asks both registries. *This is the deliverable.*
- [ ] **T11 [AC10, AC11]** `mvn test`, then `--report` on both seeds. Commit on
      `feature/us-8.1-template-method`.

---

# US-8.2 — Steps a shape must answer, may answer, cannot touch · 3 pts

**As a** developer
**I want** each step of the algorithm to declare how much freedom a subclass has over it
**So that** the compiler enforces the difference instead of a comment asking politely.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `LevelGenerator`, **then** `addShortcuts(...)` and `decorate(...)` are **concrete with empty bodies** — hooks, not primitives. |
| **AC2** | **Given** `LevelGenerator`, **then** `enforceReachability(...)`, `createRooms(...)` and `populateRooms(...)` are **private**, so no subclass can call them at all. |
| **AC3** | **Given** the shapes, **then** **exactly one** overrides a hook. |
| **AC4** | **When** the cave's hook runs, **then** a cave has measurably more doors than a corridor over eight **different** seeds. |
| **AC5** | **Given** a test, **then** it asserts by reflection that `generate()` is `final` and that the three fixed steps are private. |

### Sub-tasks

- [ ] **T1 [AC1]** Add `protected void addShortcuts(DungeonLevel level) { }` and
      `protected void decorate(DungeonLevel level, ThemeKit theme) { }`. **Empty bodies.** The
      empty body is what makes them hooks; an abstract method would force all four shapes to
      write something three of them have nothing to say about.
- [ ] **T2 [AC1]** Call both from the template, after `connectRooms` and before the invariant.
- [ ] **T3 [AC3, AC4]** Override `addShortcuts` in `CaveGenerator` only, to add one or two extra
      edges. **Loop until `tryConnect` succeeds** — see US-8.3 — because a single random attempt
      usually fails and you will think the hook is never called.
- [ ] **T4 [AC2]** Make `createRooms`, `populateRooms` and `enforceReachability` **private**. Ask
      yourself why `protected` would be worse; the answer is the point of the story. *(A step a
      subclass can call is a step a subclass can call at the wrong time, or twice, or never.)*
- [ ] **T5 [AC5]** Write the two reflection tests. They are the only tests in the project that
      assert on *structure* rather than behaviour, and they exist because here the structure is
      the feature.
- [ ] **T6 [AC4]** Write the door-counting test. **Use eight different seeds**, not
      `resetForTests()` eight times — the same seed eight times measures one dungeon eight times.
- [ ] **T7** `mvn test`. Commit on `feature/us-8.2-hooks`.

---

# US-8.3 — A shape cannot spend a direction the staircase needs · 1 pt

**As a** player
**I want** walking down to always mean descending to the next level
**So that** I can trust the one direction that matters for finishing the game.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **When** I grep the shape classes for the descending direction, **then** there are **zero matches** — shapes use only the four compass directions. |
| **AC2** | **Given** `LevelGenerator`, **then** a `tryConnect(...)` helper exists that refuses rather than overwriting an occupied door, and returns whether it acted. |
| **AC3** | **Given** any shape, **when** it generates a level, **then** every door leads both ways. |
| **AC4** | **Given** any shape, **when** it generates a level of 8 rooms, **then** the finished level still has **8 rooms** — the invariant found nothing to prune. |

### Sub-tasks

- [ ] **T1 [AC2]** Write `tryConnect(a, direction, b)`: return `false` without acting if either
      side's slot is taken.
- [ ] **T2 [AC1]** Add a `protected static final String[] DIRECTIONS` holding the four compass
      directions and **not** the descending one. Have every shape draw from it.
- [ ] **T3 [AC4]** Rework `CaveGenerator` to keep trying parents until `tryConnect` succeeds, so
      every room gets attached.
- [ ] **T4 [AC3, AC4]** Write both tests. AC4 is the interesting one: *the invariant should be a
      net that never catches anything, not a bin.* A shape that relies on pruning to tidy up is
      throwing away rooms the player was meant to explore.
- [ ] **T5** `mvn test`. Commit on `feature/us-8.3-directions`.

---

# US-8.4 — A fourth shape, and the receipt for it · 2 pts

**As a** developer
**I want** to add a fourth level shape and measure exactly what it cost
**So that** "Template Method removes duplication" becomes a number instead of a claim.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `SpokeGenerator`, **then** it builds a hub with corridors radiating off it, and contains **no** room creation, entrance handling, populate loop or breadth-first search. |
| **AC2** | **When** I run `git diff --stat` for the commit that adds it, **then** it shows **one new file** and **one modified file with one line added**. |
| **AC3** | **Given** `GameWorld.java`, **then** it is **byte-identical** before and after this story. |
| **AC4** | **When** I run `--report` afterwards on both seeds, **then** the new level shows **0 orphans and 0 spawn** — and I wrote neither invariant. |
| **AC5** | **Given** `docs/evidence.week10.md` §4, **then** it records what this shape would have cost in the old design. |

### Sub-tasks

- [ ] **T1 [AC1]** Write `SpokeGenerator`: `connectRooms` and `shapeName`. Deal the rooms into
      four arms so the hub spends each of its four doors once.
- [ ] **T2 [AC2, AC3]** Add one line to `GeneratorRegistry`. **Commit only these two files** —
      anything else voids the receipt.
- [ ] **T3 [AC2]** Run `git diff --stat HEAD~1` and paste it into `docs/evidence.week10.md` §4.
- [ ] **T4 [AC4]** Run `--report`. Note that your new shape satisfies two invariants you never
      implemented. Write down why.
- [ ] **T5 [AC5]** Estimate the old design's cost for the same shape, using the starter's
      `GameWorld` as the reference: which method would you have copied, how many lines, and — the
      question that matters — **which of that method's bugs would you have copied with it?**
- [ ] **T6** `mvn test`. Commit on `feature/us-8.4-spoke`.

---

## Risks

| Risk | Mitigation |
|---|---|
| Leaving `generate()` non-final "for flexibility" | Then a shape can reorder the steps and the whole week was decorative. AC2 is a reflection test. |
| Making the fixed steps `protected` instead of `private` | A step a subclass can call is a step it can call at the wrong time. US-8.2 T4. |
| Making the hooks `abstract` | Then all four shapes must implement something three of them have nothing to say about. The empty body *is* the design. |
| Concrete generators that grow to 40 lines | You moved a fixed step into a subclass. Each shape should be `connectRooms` plus a name. |
| A shape using the descending direction | It collides with the staircase and breaks the one move that finishes the game. AC1 is a grep. |
| Relying on `enforceReachability` to tidy up a sloppy shape | It will, by deleting rooms. AC4 of US-8.3 catches the room loss. |
| Judging the fix on one seed | The ring bug is invisible on the default seed. Check both, every time. AC10 of US-8.1 requires it. |
| Committing US-8.4 alongside anything else | The receipt is a `git diff --stat`. One dirty commit and the measurement is gone. |
| Treating the carry-over as optional because it is not this week's pattern | Debt deferred twice becomes the design. US-7.5 is two points; clear it Monday. |
| Reaching for `instanceof WeaponDecorator` in `DisenchantCommand` | It compiles and US-7.1's grep does not look in `commands/`. Put `peel()` on the interface instead. US-7.5 T4. |
| Writing an `undo()` on `DisenchantCommand` | The snapshot already restores inventory and hand. You will restore twice. US-7.5 T7. |
| Skipping US-7.5 T9 because comments are not code | Three files name the wrong enchantment. Nothing will ever fail a test over it, which is exactly why it is on the list. |
| An absence assertion tripped by your own comment | Strip comments before searching. It happened to me eight times building this package; `codeOnly()` is in `StateTest` already. |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| **US-7.5** (carry-over) | **2** | | |
| US-8.1 | 4 | | |
| US-8.2 | 3 | | |
| US-8.3 | 1 | | |
| US-8.4 | 2 | | |

**Points completed:** ____ · **Running velocity:** ____

**Also fill in Sprint 7's row, retrospectively.** `docs/sprint-07-plan.md` has an empty
calibration table and so do Sprints 3, 5 and 6. Five sprints with no actuals means nobody —
including you — knows what this team's capacity is. Estimate from your commit timestamps if you
have to; a rough number you wrote down beats an exact number you never took.

## Sprint Review — one sentence

## Retro — one thing to keep, one thing to change
