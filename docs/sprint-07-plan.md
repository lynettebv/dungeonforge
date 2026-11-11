# Sprint 7 Plan — Week 9 — Decorator — SUPPLIED

**Epic:** E8, Enchantments · **Pattern:** Decorator (Head First Ch. 3)

> **Still supplied — stories, acceptance criteria and sub-tasks**, with each sub-task tagged
> `[AC3]` for the criterion it serves. Nine worked sprints now. Read the tags.

## Sprint Goal

> Enchantments stack — any combination, any depth, any order — and adding a new one costs one
> class and one line.

## Capacity

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 3 (Wk 5) | 10 | ____ |
| Sprint 5 (Wk 7) | 10 | ____ |
| Sprint 6 (Wk 8) | 12 | ____ |
| **Sprint 7 (Wk 9)** | **11** | ____ |

## Committed stories

| Issue | Story | Points |
|---|---|---|
| **US-6.7** | **Carried over from Sprint 6** — one answer to "is the game over?" | **1** |
| US-7.1 | One class per enchantment, not one class per combination | 4 |
| US-7.2 | Enchant anything, any number of times, in any order | 3 |
| US-7.3 | A fourth enchantment, and the receipt for it | 1 |
| US-7.4 | Order is meaning, not style | 2 |

**Capacity:** ~10 points · **Committed:** 11 points (10 new + **1 carried over**)

> **About the carry-over.** US-6.7 is Sprint 6 debt, and it is listed first because that is what
> carried-over work does: it arrives before the new work and it spends capacity you were hoping
> to use on something else. Sprint 6's US-6.2 asked for a thing it then did not deliver — the
> game-over rule was supposed to stop being a condition repeated at every call site, and instead
> it ended up written in three places. That bill comes due here.
>
> If Sprint 6 came in under 9, drop **US-7.4** and say so in your retro. Do not drop the
> carry-over; debt that gets deferred twice stops being debt and becomes the design.

## Suggested order

1. **US-6.7 first.** One point, unrelated to Decorator, and it is debt. Clearing it takes under
   an hour and gets the board honest before the real work starts.
2. **US-7.1 second, and it is most of the sprint.** **The deletion of the four combination
   classes is the deliverable** — not the creation of `WeaponDecorator`.
3. **US-7.2** — the registry, then the two-line fix to the equipped hand.
4. **US-7.3** — twenty minutes, and it is the story your Part D evidence comes from.
5. **US-7.4** — two tests and a paragraph. Do not skip the paragraph.

---

# US-6.7 — One answer to "is the game over?" · 1 pt · CARRIED OVER FROM SPRINT 6

**As a** developer
**I want** exactly one place that decides the game is over
**So that** I am not maintaining the same two-clause condition in three files that can disagree.

### Why this is carry-over and not a new feature

Sprint 6's US-6.2 said the game-over rule should live in a state instead of being a flag that
every caller checks. **It half happened.** `GameOverState` was built, and then `syncState()` was
written like this:

```java
if (!player.isAlive() || !running) { transitionTo(new GameOverState()); return; }
```

So the mode is decided partly from the world (`isAlive`) and partly from a **session flag**
(`running`). And because the loop still had to know when to stop, the same two-clause rule got
written out again — twice:

| Where | The rule, spelled out |
|---|---|
| `GameContext.syncState()` | `!player.isAlive() \|\| !running` |
| `Main.gameLoop()` | `while (ctx.isRunning() && ctx.getPlayer().isAlive())` |
| `Main.runScript()` | `if (!ctx.isRunning() \|\| !ctx.getPlayer().isAlive()) break;` |

Three copies of one rule. US-6.1 made exactly this complaint about combat-mode guards and got
them down to zero; `syncState()` then quietly reintroduced the pattern one story later.

**And it had a consequence nobody noticed.** `GameOverState.availableCommands()` lists four
verbs, and **not one of them was ever reachable**, because the loop exited the instant the
player died or quit. The story was called *"death is somewhere you stay"* and you could not stay
there. Dead code does not announce itself.

### The insight the fix turns on

`running` is **not** a duplicate of `GameOverState`. They are two different facts:

| | Means | Who may read it |
|---|---|---|
| `GameState.isTerminal()` | the **adventure** is over — dead, or walked out | the game loop, the parser |
| `GameContext.running` | the **session** is over — stop reading input | the game loop, and nothing else |

You can be in a terminal state with the session still running: that is precisely what *reading
your quest log after you die* looks like. **The defect was never that the flag existed. It was
that `syncState()` read it.**

So `running` stays. It just stops being consulted by anything deciding a *mode*.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `GameState`, **then** it answers `isTerminal()`, and `GameOverState` is the only state that returns true. |
| **AC2** | **Given** `syncState()`, **when** I strip its comments and search the body for the session flag, **then** there are **zero** matches. |
| **AC3** | **Given** `Main`, **then** neither the loop condition nor the script runner re-implements the game-over rule — each asks **one** question. |
| **AC4** | **Given** the player's hit points reach zero, **when** the mode syncs, **then** the state is terminal **and** the session is still running. |
| **AC5** | **Given** a terminal state, **when** `syncState()` runs again (even with the player healed), **then** the state does **not** change. |
| **AC6** | **Given** Game Over mode, **then** `quests`, `history`, `status` and `quit` are available and movement verbs are not. |
| **AC7** | **Given** `quit`, **when** it executes, **then** the adventure **and** the session both end. |
| **AC8** | **When** I run `mvn test`, **then** all 86 tests that passed before still pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC1]** Add `default boolean isTerminal() { return false; }` to `GameState`.
      **Do this first** — the tests for this story will not compile until it exists.
- [ ] **T2 [AC1]** Override it to `true` in `GameOverState`, and only there.
- [ ] **T3 [AC5]** In `syncState()`, make `if (state.isTerminal()) return;` the **first** line.
      Order matters: put it after the death check and a healed quitter gets dragged back into
      Exploring.
- [ ] **T4 [AC2, AC4]** Reduce the death branch to `if (!player.isAlive())` and **delete the
      `|| !running`**.
- [ ] **T5 [AC7]** Have `quit` do both jobs explicitly: `transitionTo(new GameOverState())` and
      `stop()`. Two facts, one action — which is not the same as conflating them.
- [ ] **T6 [AC3]** Collapse `Main`'s loop condition to `while (ctx.isRunning())` and the script
      runner's to `if (!ctx.isRunning()) break;`.
- [ ] **T7 [AC4, AC5, AC6, AC7]** Four tests, one per behaviour. Put them in `StateTest`.
- [ ] **T8 [AC2, AC3]** Two **absence** assertions that read the source. **Strip comments before
      you search** — see the note below; this one has teeth.
- [ ] **T9 [AC8]** `mvn test`. Commit on `feature/us-6.7-one-game-over-rule`.

> **T8, and a mistake worth inheriting rather than making yourself.**
>
> A source-inspection assertion written as `source.contains("running")` is tripped by a
> **comment** that merely mentions the word — including the comment you will write explaining
> that the code deliberately does not read it. Strip comments first:
>
> ```java
> private static String codeOnly(String source) {
>     return source.replaceAll("(?s)/\\*.*?\\*/", "")
>                  .replaceAll("(?m)//.*$", "");
> }
> ```
>
> **Grep the code, not the commentary.**

---

# US-7.1 — One class per enchantment, not one class per combination · 4 pts

**As a** player
**I want** a weapon's enchantments to combine freely
**So that** the game can offer a Flaming Frost Blade without somebody having written a class
called `FlamingFrostWeapon`.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** the `items` package, **then** `Weapon` is an *interface* and `BasicWeapon` is the only plain implementation of it. |
| **AC2** | **Given** the `items` package, **then** `WeaponDecorator` is abstract, implements `Weapon`, and holds a `protected final Weapon wrapped`. |
| **AC3** | **Given** the `items` package, **then** exactly three concrete decorators exist: Flaming, Frost, Vampiric. |
| **AC4** | **When** I run `ls items/ \| grep -E '(FlamingFrost\|FlamingVampiric\|FrostVampiric)'`, **then** there are **zero matches**. |
| **AC5** | **When** I grep the `items` package for the fire damage bonus, **then** it appears in **exactly one file** (it starts in five). |
| **AC6** | **When** I run `grep -o instanceof items/*.java`, **then** there are **zero matches** (it starts at 34). |
| **AC7** | **When** I run `mvn test`, **then** all 86 tests that passed before this story still pass, unchanged. |

### Sub-tasks

- [ ] **T1 [AC1]** Rename the class in `Weapon.java` to `BasicWeapon` and put it in
      `BasicWeapon.java`. Do not change its body.
- [ ] **T2 [AC1]** Write the new `Weapon.java` as an interface: `extends Item`, declaring
      `int getDamage()` and `default int lifesteal() { return 0; }`.
- [ ] **T3 [AC1, AC7]** Run `mvn compile`. **Let the compiler find the call sites** — do not grep
      for them first.
      <br><br>
      It reports errors in **five files once the doomed classes are gone**: the four `ThemeKit`
      loot lines and the starting weapon in `Main`. If you have not deleted the combination
      classes and `EnchantmentForge` yet, the list is much longer — **most of it is code you are
      about to delete, so do not try to make it compile.**
      <br><br>
      **Expect a red build between US-7.1 and US-7.2.** `EnchantCommand` is written against
      `EnchantmentForge`, so it cannot compile until US-7.2 replaces it with the registry. That
      is the honest shape of a refactor this size: treat US-7.1 and US-7.2 as one sitting with a
      single green checkpoint at the end.
- [ ] **T4 [AC2]** Write `WeaponDecorator`: abstract, `implements Weapon`, one
      `protected final Weapon wrapped` assigned in a constructor that rejects null. Delegate
      `getName`, `getWeight`, `getValue`, `getDamage`, `lifesteal` to `wrapped`.
- [ ] **T5 [AC2]** Give `WeaponDecorator` two abstract hooks — `prefix()` and `valueBonus()` —
      and one `describe()` built from them, so no concrete decorator writes `describe()`.
- [ ] **T6 [AC3, AC5]** Write `FlamingWeapon` and `FrostWeapon`. Each overrides **only**
      `getDamage()`, plus the two hooks. If either is longer than about eight lines, you are
      overriding something you should be delegating.
- [ ] **T7 [AC3]** Write `VampiricWeapon`. It overrides **only** `lifesteal()`, and it drinks
      from `wrapped.getDamage()` — *not* `getDamage()`. Get this right now and US-7.4 is twenty
      minutes.
- [ ] **T8 [AC4]** Delete `FlamingFrostWeapon`, `FlamingVampiricWeapon`, `FrostVampiricWeapon`,
      `FlamingFrostVampiricWeapon`. **This is the deliverable.**
- [ ] **T9 [AC6]** Change each of the four `ThemeKit` loot lines to wrap a `BasicWeapon` instead
      of naming a combination class. Fix `Main`'s starting weapon the same way.
- [ ] **T10 [AC7]** `mvn test`. Commit on `feature/us-7.1-decorator`.

---

# US-7.2 — Enchant anything, any number of times, in any order · 3 pts

**As a** player
**I want** `enchant` to work on any weapon I am carrying, however enchanted it already is
**So that** the game never refuses a reasonable request for a reason that is really about
somebody's class hierarchy.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `EnchantmentRegistry`, **then** it holds a `Map<String, UnaryOperator<Weapon>>` and each enchantment is **one line**. |
| **AC2** | **When** I run `grep -rn EnchantmentForge src/`, **then** there are **zero matches**. |
| **AC3** | **Given** any known enchantment and any weapon at any depth, **when** `apply()` is called, **then** it returns a weapon and **never returns null** (the old class had 13 such paths). An *unknown* enchantment throws instead. |
| **AC4** | **When** I enchant a weapon with `flaming` twice, **then** I get `Flaming Flaming Notched Shortsword [dmg 13]` and no error. |
| **AC5** | **Given** the weapon in my hand, **when** I enchant it, **then** `status` shows the higher damage **in the same turn**. |
| **AC6** | **Given** an `enchant` I just ran, **when** I `undo`, **then** both my inventory and my hand hold the original weapon. |

### Sub-tasks

- [ ] **T1 [AC1]** Write `EnchantmentRegistry` with a `LinkedHashMap<String,
      UnaryOperator<Weapon>>` and three entries, each a constructor reference
      (`FlamingWeapon::new`).
- [ ] **T2 [AC1]** Add `isKnown(String)` and `known()` so callers never hardcode the list.
      `EnchantCommand`'s help and error messages both read from `known()`.
- [ ] **T3 [AC3]** Write `apply(name, weapon)`: look up, throw `IllegalArgumentException` if
      absent, otherwise return `spell.apply(weapon)`. **There is no branch that returns null.**
- [ ] **T4 [AC2]** Rewrite `EnchantCommand` against the registry, then delete
      `EnchantmentForge.java`. Run the grep in AC2 and expect zero.
- [ ] **T5 [AC5]** In `EnchantCommand.doExecute()`, record
      `wasEquipped = (player().getEquippedWeapon() == w)` **before** the swap, and call
      `player().equip(after)` after it. Two lines.
- [ ] **T6 [AC6]** Add a `Weapon equipped` field to `TurnSnapshot` — capture it in the
      constructor, restore it in `restore()`. **This is the third time that class has had to
      grow**: Week 7 built it, Week 8 taught it monster states, Week 9 gives it the hand.
- [ ] **T7 [AC4, AC5, AC6]** One test per criterion in a new `DecoratorTest`.
- [ ] **T8** `mvn test`. Commit on `feature/us-7.2-registry`.

> **T5 is not the pattern.** Decorator did not fix the stale-hand bug and was never going to;
> those two lines would have worked in the starter. Say so in your retro. Knowing what a pattern
> *did not* do for you is worth as much as knowing what it did.

---

# US-7.3 — A fourth enchantment, and the receipt for it · 1 pt

**As a** developer
**I want** to add a fourth enchantment and measure exactly what it cost
**So that** "Decorator scales better" stops being something I was told and becomes something I
counted.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** `KeenWeapon`, **then** it *multiplies* base damage rather than adding to it, proving a decorator is not restricted to `+N`. |
| **AC2** | **When** I run `git diff --stat` for the commit that adds it, **then** it shows **exactly one new file** and **exactly one modified file with exactly one line added**. |
| **AC3** | **When** I run `mvn test`, **then** every test for the other three enchantments passes **without being edited**. |

### Sub-tasks

- [ ] **T1 [AC1]** Write `KeenWeapon extends WeaponDecorator`, overriding `getDamage()` to return
      `Math.round(wrapped.getDamage() * 1.5)`.
- [ ] **T2 [AC2]** Add exactly one line to `EnchantmentRegistry`. Commit **only these two
      files** — if anything else is in the commit, the receipt is void.
- [ ] **T3 [AC2]** Run `git diff --stat HEAD~1` and paste it into `docs/evidence.week9.md` §3.
      That output is the graded artifact, not the class.
- [ ] **T4 [AC3]** `mvn test`. Commit on `feature/us-7.3-keen`.

---

# US-7.4 — Order is meaning, not style · 2 pts

**As a** developer
**I want** to know what wrapping order changes
**So that** I stop treating the nesting order as arbitrary and start treating it as a decision.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** a Keen and a Flaming decorator, **when** applied in each order, **then** a test asserts **both** resulting damage numbers by value and that they differ. |
| **AC2** | **Given** a Vampiric and a Flaming decorator, **when** applied in each order, **then** a test asserts both lifesteal numbers by value and that they differ. |
| **AC3** | **Given** a three-deep stack, **when** I call `describe()`, **then** all three enchantments appear, outermost first. |
| **AC4** | **Given** `docs/evidence.week9.md` §4, **then** it contains one paragraph explaining why AC1 and AC2 describe correct behaviour rather than a bug. |

### Sub-tasks

- [ ] **T1 [AC1]** Write `orderChangesTheResultForMultiplyingDecorators`. Assert the actual
      numbers (16 and 18), not merely that they differ — a test that only says "not equal" passes
      even when both are wrong.
- [ ] **T2 [AC2]** Write `orderChangesHowMuchAVampiricWeaponDrinks`. Same rule: name both
      numbers (3 and 2).
- [ ] **T3 [AC3]** Write a `describe()` test on a three-deep stack.
- [ ] **T4 [AC4]** Write the paragraph. One idea, stated plainly: *a decorator can only see what
      is underneath it*. Everything else follows from that sentence.
- [ ] **T5** `mvn test`. Commit on `feature/us-7.4-order`.

---

## Risks

| Risk | Mitigation |
|---|---|
| Treating the carry-over as optional | Debt deferred twice becomes the design. US-6.7 is one point; clear it Monday. |
| Putting the terminal guard after the death check in `syncState()` | A healed quitter gets dragged back to Exploring. AC5 of US-6.7 is a test. |
| An absence assertion tripped by your own comment | Strip comments before searching. US-6.7 T8. |
| Building `WeaponDecorator` and then *keeping* the combination classes "just in case" | The sprint is not done until they are deleted. AC4 is a grep, and it is graded. |
| Vampiric reading `getDamage()` instead of `wrapped.getDamage()` | Infinite recursion in some stacks, and US-7.4 becomes unprovable. T7 of US-7.1 calls this out. |
| Confusing Decorator with inheritance because `WeaponDecorator` shares the component type | Part D1 makes you count how many files know the fire bonus. 5 versus 1 is the difference. |
| Thinking the pattern fixed the stale-hand bug | It did not. US-7.2 T5 says so; repeat it in your retro. |
| Adding `getDamage()` overrides to every decorator out of habit | Only three of the four override it. Delegation is the default. |
| Committing US-7.3 alongside anything else | AC2 is a `git diff --stat`. One dirty commit and the measurement is gone. |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| US-6.7 | 1 | | |
| US-7.1 | 4 | | |
| US-7.2 | 3 | | |
| US-7.3 | 1 | | |
| US-7.4 | 2 | | |

**Points completed:** ____ · **Running velocity:** ____

## Sprint Review — one sentence

## Retro — one thing to keep, one thing to change
